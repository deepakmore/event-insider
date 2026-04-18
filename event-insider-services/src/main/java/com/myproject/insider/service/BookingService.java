package com.myproject.insider.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myproject.insider.dto.request.BookingRequest;
import com.myproject.insider.dto.response.BookingLineResponse;
import com.myproject.insider.dto.response.BookingResponse;
import com.myproject.insider.entity.Booking;
import com.myproject.insider.entity.BookingSeat;
import com.myproject.insider.entity.EventShow;
import com.myproject.insider.entity.PaymentWebhookReceipt;
import com.myproject.insider.entity.SeatInventory;
import com.myproject.insider.entity.ShowSeatPricing;
import com.myproject.insider.entity.User;
import com.myproject.insider.enums.BookingStatus;
import com.myproject.insider.enums.SeatInventoryStatus;
import com.myproject.insider.exception.ApiBadRequestException;
import com.myproject.insider.exception.ApiConflictException;
import com.myproject.insider.exception.ResourceNotFoundException;
import com.myproject.insider.kafka.BookingCompletionKafkaTrigger;
import com.myproject.insider.repository.BookingRepository;
import com.myproject.insider.repository.BookingSeatRepository;
import com.myproject.insider.repository.EventShowRepository;
import com.myproject.insider.repository.PaymentWebhookReceiptRepository;
import com.myproject.insider.repository.SeatInventoryRepository;
import com.myproject.insider.repository.ShowSeatPricingRepository;
import com.myproject.insider.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final UserRepository userRepository;
    private final EventShowRepository eventShowRepository;
    private final ShowSeatPricingRepository showSeatPricingRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final PaymentWebhookReceiptRepository paymentWebhookReceiptRepository;
    private final BookingCompletionKafkaTrigger bookingCompletionKafkaTrigger;

    @Value("${app.booking.hold-duration:PT15M}")
    private String bookingHoldDurationIso;

    @Transactional
    public BookingResponse create(BookingRequest request) {
        List<Long> distinctSeatInventoryIds = request.getSeatInventoryIds().stream().distinct().sorted().toList();
        if (distinctSeatInventoryIds.isEmpty()) {
            throw new ApiBadRequestException("seatInventoryIds must not be empty");
        }
        if (distinctSeatInventoryIds.size() != request.getSeatInventoryIds().size()) {
            throw new ApiBadRequestException("seatInventoryIds must not contain duplicates");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.getUserId()));
        EventShow show = eventShowRepository.findByIdAndDisabledFalse(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("EventShow", request.getShowId()));

        ShowSeatPricing pricing = showSeatPricingRepository
                .findByEventShow_IdAndEventCategory_Id(request.getShowId(), request.getEventSeatCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("ShowSeatPricing",
                        request.getShowId() + "/" + request.getEventSeatCategoryId()));
        if (pricing.getEventShow().isDisabled() || pricing.getEventCategory().isDisabled()) {
            throw new ResourceNotFoundException("ShowSeatPricing", request.getShowId() + "/" + request.getEventSeatCategoryId());
        }
        if (!Objects.equals(pricing.getEventShow().getEvent().getId(), pricing.getEventCategory().getEvent().getId())) {
            throw new ApiBadRequestException("Seat category does not match the show's event");
        }

        List<SeatInventory> inventories = seatInventoryRepository.findAllById(distinctSeatInventoryIds);
        if (inventories.size() != distinctSeatInventoryIds.size()) {
            throw new ApiBadRequestException("One or more seat inventory ids are invalid");
        }
        for (SeatInventory inv : inventories) {
            if (!Objects.equals(inv.getEventShow().getId(), request.getShowId())) {
                throw new ApiBadRequestException("All seats must belong to the requested show");
            }
        }

        BigDecimal unitPrice = pricing.getBasePrice();
        Instant now = Instant.now();
        Booking booking = Booking.builder()
                .user(user)
                .eventShow(show)
                .status(BookingStatus.IN_PROGRESS)
                .createdAt(now)
                .holdExpiresAt(now.plus(Duration.parse(bookingHoldDurationIso)))
                .build();
        bookingRepository.saveAndFlush(booking);

        int held = seatInventoryRepository.holdSeatsIfAvailable(distinctSeatInventoryIds, SeatInventoryStatus.AVAILABLE,
                SeatInventoryStatus.HELD, booking.getId(), request.getShowId(), now);
        if (held != distinctSeatInventoryIds.size()) {
            throw new ApiConflictException("One or more seats are no longer available");
        }

        for (Long seatInventoryId : distinctSeatInventoryIds) {
            BookingSeat line = BookingSeat.builder()
                    .bookingId(booking.getId())
                    .seatInventoryId(seatInventoryId)
                    .price(unitPrice)
                    .build();
            bookingSeatRepository.save(line);
        }

        return toResponse(booking.getId());
    }

    @Transactional(readOnly = true)
    public BookingResponse findForUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findByIdAndUser_Id(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        return toResponse(booking.getId());
    }

    @Transactional
    public void cancelByUser(Long bookingId, Long userId, BookingRequest request) {
        Booking booking = bookingRepository.findByIdAndUser_Id(bookingId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        booking.setCancelReason(resolveUserCancelReason(request));
        cancelInProgress(booking, request.getCancelReason(), request.getUserId());
    }

    private String resolveUserCancelReason(BookingRequest request) {
        if (request == null || request.getCancelReason() == null || request.getCancelReason().isBlank()) {
            return "User cancelled";
        }
        String reason = request.getCancelReason().strip();
        if (reason.length() > 100) {
            throw new ApiBadRequestException("cancelReason must be at most 100 characters");
        }
        return reason;
    }

    @Transactional
    public void cancelExpiredHold(Long bookingId) {
        bookingRepository.findByIdForUpdate(bookingId).ifPresent(booking -> {
            if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
                return;
            }
            if (booking.getHoldExpiresAt() == null || !booking.getHoldExpiresAt().isBefore(Instant.now())) {
                return;
            }
            cancelInProgress(booking, "Hold expired", null);
        });
    }

    private void cancelInProgress(Booking booking, String cancelReason, Long cancelledByUserId) {
        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new ApiBadRequestException("Only in-progress bookings can be cancelled");
        }
        Instant now = Instant.now();
        seatInventoryRepository.releaseHeldSeats(booking.getId(), SeatInventoryStatus.HELD, SeatInventoryStatus.AVAILABLE,
                now);
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(now);
        booking.setCancelReason(cancelReason);
        booking.setCancelledByUserId(cancelledByUserId);
        bookingRepository.save(booking);
    }

    @Transactional
    public void confirmPaidFromWebhook(String provider, String externalEventId, long bookingId, String paymentStatus) {
        if (!"SUCCEEDED".equalsIgnoreCase(paymentStatus)) {
            throw new ApiBadRequestException("Unsupported paymentStatus");
        }
        Optional<PaymentWebhookReceipt> prior = paymentWebhookReceiptRepository.findByProviderAndExternalEventId(provider,
                externalEventId);
        if (prior.isPresent()) {
            if (!prior.get().getBooking().getId().equals(bookingId)) {
                throw new ApiBadRequestException("externalEventId already used for another booking");
            }
            return;
        }
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        if (booking.getStatus() == BookingStatus.COMPLETE) {
            return;
        }
        if (booking.getStatus() != BookingStatus.IN_PROGRESS) {
            throw new ApiBadRequestException("Booking is not awaiting payment");
        }
        int expectedLines = bookingSeatRepository.findByBookingId(bookingId).size();
        Instant now = Instant.now();
        int updated = seatInventoryRepository.markSeatsBooked(bookingId, SeatInventoryStatus.HELD,
                SeatInventoryStatus.BOOKED, now);
        if (updated != expectedLines) {
            throw new ApiConflictException("Seat state mismatch during confirmation");
        }
        booking.setStatus(BookingStatus.COMPLETE);
        booking.getUser().getId();
        booking.getEventShow().getId();
        bookingRepository.save(booking);
        bookingCompletionKafkaTrigger.publishComplete(booking);
        PaymentWebhookReceipt receipt = PaymentWebhookReceipt.builder()
                .provider(provider)
                .externalEventId(externalEventId)
                .booking(booking)
                .createdAt(now)
                .build();
        paymentWebhookReceiptRepository.save(receipt);
    }

    private BookingResponse toResponse(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ResourceNotFoundException("Booking", bookingId));
        List<BookingSeat> lines = bookingSeatRepository.findByBookingId(bookingId);
        List<BookingLineResponse> lineResponses = lines.stream()
                .map(l -> BookingLineResponse.builder()
                        .seatInventoryId(l.getSeatInventoryId())
                        .unitPrice(l.getPrice())
                        .build())
                .toList();
        BigDecimal total = lineResponses.stream()
                .map(BookingLineResponse::getUnitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return BookingResponse.builder()
                .id(booking.getId())
                .userId(booking.getUser().getId())
                .showId(booking.getEventShow().getId())
                .status(booking.getStatus())
                .createdAt(booking.getCreatedAt())
                .holdExpiresAt(booking.getHoldExpiresAt())
                .cancelledAt(booking.getCancelledAt())
                .cancelReason(booking.getCancelReason())
                .cancelledByUserId(booking.getCancelledByUserId())
                .totalAmount(total)
                .lines(lineResponses)
                .build();
    }
}