package com.myproject.insider.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myproject.insider.dto.response.SeatResponse;
import com.myproject.insider.dto.request.SeatUpsertRequest;
import com.myproject.insider.entity.EventShow;
import com.myproject.insider.entity.Seat;
import com.myproject.insider.exception.ApiBadRequestException;
import com.myproject.insider.exception.ResourceNotFoundException;
import com.myproject.insider.repository.EventShowRepository;
import com.myproject.insider.repository.SeatInventoryRepository;
import com.myproject.insider.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SeatService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "seatNumber");

    private static final Set<String> ALLOWED_SORT_PROPERTIES =
            Set.of("id", "seatNumber", "rowLabel", "columnNumber", "createdAt");

    private final SeatRepository seatRepository;
    private final EventShowRepository eventShowRepository;
    private final SeatInventoryRepository seatInventoryRepository;

    @Transactional
    public SeatResponse create(SeatUpsertRequest request) {
        Seat seat = Seat.builder().build();
        apply(request, seat);
        seat.setCreatedAt(Instant.now());
        return toResponse(seatRepository.save(seat));
    }

    @Transactional(readOnly = true)
    public SeatResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<SeatResponse> findAll(Pageable pageable) {
        return seatRepository.findAll(toSafePageable(pageable)).map(this::toResponse);
    }

    @Transactional
    public SeatResponse update(Long id, SeatUpsertRequest request) {
        Seat seat = getEntity(id);
        if (!seat.getEventShow().getId().equals(request.getShowId()) && seatInventoryRepository.existsBySeat_Id(id)) {
            throw new ApiBadRequestException("Cannot change show when this seat has inventory rows");
        }
        apply(request, seat);
        return toResponse(seatRepository.save(seat));
    }

    @Transactional
    public void delete(Long id) {
        if (!seatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Seat", id);
        }
        seatRepository.deleteById(id);
    }

    private Seat getEntity(Long id) {
        return seatRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Seat", id));
    }

    private void apply(SeatUpsertRequest request, Seat seat) {
        EventShow show = eventShowRepository.findByIdAndDisabledFalse(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("EventShow", request.getShowId()));
        seat.setEventShow(show);
        seat.setSeatNumber(request.getSeatNumber());
        seat.setRowLabel(request.getRowLabel());
        seat.setColumnNumber(request.getColumnNumber());
        seat.setXCoordinate(request.getXCoordinate());
        seat.setYCoordinate(request.getYCoordinate());
    }

    private SeatResponse toResponse(Seat seat) {
        return SeatResponse.builder()
                .id(seat.getId())
                .showId(seat.getEventShow().getId())
                .seatNumber(seat.getSeatNumber())
                .rowLabel(seat.getRowLabel())
                .columnNumber(seat.getColumnNumber())
                .xCoordinate(seat.getXCoordinate())
                .yCoordinate(seat.getYCoordinate())
                .createdAt(seat.getCreatedAt())
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