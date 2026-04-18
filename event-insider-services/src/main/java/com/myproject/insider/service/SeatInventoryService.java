package com.myproject.insider.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myproject.insider.dto.response.SeatInventoryResponse;
import com.myproject.insider.dto.response.SeatInventoryWithSeatResponse;
import com.myproject.insider.dto.request.SeatInventoryUpsertRequest;
import com.myproject.insider.entity.EventShow;
import com.myproject.insider.entity.Seat;
import com.myproject.insider.entity.SeatInventory;
import com.myproject.insider.enums.SeatInventoryStatus;
import com.myproject.insider.exception.ApiBadRequestException;
import com.myproject.insider.exception.ResourceNotFoundException;
import com.myproject.insider.repository.EventShowRepository;
import com.myproject.insider.repository.SeatInventoryRepository;
import com.myproject.insider.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatInventoryService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "id");

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "status", "updatedAt", "version");

    private final SeatInventoryRepository seatInventoryRepository;
    private final EventShowRepository eventShowRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public SeatInventoryResponse create(SeatInventoryUpsertRequest request) {
        if (request.getBookingId() != null) {
            throw new ApiBadRequestException("bookingId must be null when creating seat inventory");
        }
        SeatInventory entity = SeatInventory.builder().build();
        apply(request, entity);
        entity.setUpdatedAt(Instant.now());
        return toResponse(seatInventoryRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public SeatInventoryResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<SeatInventoryResponse> findAll(Pageable pageable) {
        return seatInventoryRepository.findAll(toSafePageable(pageable)).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<SeatInventoryWithSeatResponse> findForShowCatalog(Long showId, SeatInventoryStatus statusFilter) {
        eventShowRepository.findByIdAndDisabledFalse(showId).orElseThrow(() -> new ResourceNotFoundException("EventShow", showId));
        List<SeatInventory> rows = statusFilter == null
                ? seatInventoryRepository.findByShowIdWithSeat(showId)
                : seatInventoryRepository.findByShowAndStatusWithSeat(showId, statusFilter);
        return rows.stream().map(this::toWithSeatResponse).toList();
    }

    @Transactional
    public SeatInventoryResponse update(Long id, SeatInventoryUpsertRequest request) {
        SeatInventory entity = getEntity(id);
        apply(request, entity);
        entity.setUpdatedAt(Instant.now());
        return toResponse(seatInventoryRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!seatInventoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("SeatInventory", id);
        }
        seatInventoryRepository.deleteById(id);
    }

    private SeatInventory getEntity(Long id) {
        return seatInventoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("SeatInventory", id));
    }

    private void apply(SeatInventoryUpsertRequest request, SeatInventory entity) {
        EventShow show = eventShowRepository.findByIdAndDisabledFalse(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("EventShow", request.getShowId()));
        Seat seat = seatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat", request.getSeatId()));
        if (!Objects.equals(seat.getEventShow().getId(), show.getId())) {
            throw new ApiBadRequestException("Seat must belong to the same show as seat inventory");
        }
        entity.setEventShow(show);
        entity.setSeat(seat);
        entity.setStatus(request.getStatus());
        entity.setBookingId(request.getBookingId());
    }

    private SeatInventoryResponse toResponse(SeatInventory entity) {
        return SeatInventoryResponse.builder()
                .id(entity.getId())
                .showId(entity.getEventShow().getId())
                .seatId(entity.getSeat().getId())
                .status(entity.getStatus())
                .bookingId(entity.getBookingId())
                .version(entity.getVersion())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private SeatInventoryWithSeatResponse toWithSeatResponse(SeatInventory entity) {
        Seat seat = entity.getSeat();
        return SeatInventoryWithSeatResponse.builder()
                .id(entity.getId())
                .showId(entity.getEventShow().getId())
                .seatId(seat.getId())
                .seatNumber(seat.getSeatNumber())
                .rowLabel(seat.getRowLabel())
                .status(entity.getStatus())
                .bookingId(entity.getBookingId())
                .version(entity.getVersion())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private static Pageable toSafePageable(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_SORT);
        }
        List<Order> safe = new ArrayList<>();
        for (Order order : sort) {
            String prop = order.getProperty();
            if (prop != null && ALLOWED_SORT_PROPERTIES.contains(prop)) {
                safe.add(order);
            }
        }
        if (safe.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_SORT);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(safe));
    }
}