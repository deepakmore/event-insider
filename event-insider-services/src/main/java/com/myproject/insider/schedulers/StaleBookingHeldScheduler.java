package com.myproject.insider.schedulers;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.myproject.insider.entity.Booking;
import com.myproject.insider.enums.BookingStatus;
import com.myproject.insider.repository.BookingRepository;
import com.myproject.insider.service.BookingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class StaleBookingHeldScheduler {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    @Value("${app.booking.stale-hold-page-size:50}")
    private int staleHoldPageSize;

    @Scheduled(fixedDelayString = "${app.booking.stale-hold-scan-interval-ms:60000}")
    public void sweepExpiredHolds() {
        Instant now = Instant.now();
        int pageNumber = 0;
        Page<Booking> page;
        do {
            page = bookingRepository.findByStatusAndHoldExpiresAtBefore(
                    BookingStatus.IN_PROGRESS,
                    now,
                    PageRequest.of(pageNumber, staleHoldPageSize, Direction.ASC, "id"));
            page.getContent().forEach(b -> {
                try {
                    bookingService.cancelExpiredHold(b.getId());
                } catch (Exception ex) {
                    log.warn("Failed to cancel expired booking hold id={}", b.getId(), ex);
                }
            });
            pageNumber++;
        } while (page.hasNext());
    }
}