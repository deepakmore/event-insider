package com.myproject.insider.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.myproject.insider.dto.response.EventShowResponse;
import com.myproject.insider.dto.request.EventShowUpsertRequest;
import com.myproject.insider.entity.Event;
import com.myproject.insider.entity.EventShow;
import com.myproject.insider.entity.Venue;
import com.myproject.insider.exception.ResourceNotFoundException;
import com.myproject.insider.repository.EventRepository;
import com.myproject.insider.repository.EventShowRepository;
import com.myproject.insider.repository.VenueRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventShowService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "startTime");

    private static final Set<String> ALLOWED_SORT_PROPERTIES =
            Set.of("id", "startTime", "endTime", "createdAt");

    private final EventShowRepository eventShowRepository;
    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;

    @Transactional
    public EventShowResponse create(EventShowUpsertRequest request) {
        EventShow show = EventShow.builder().build();
        apply(request, show);
        show.setCreatedAt(Instant.now());
        show.setDisabled(false);
        return toResponse(eventShowRepository.save(show));
    }

    @Transactional(readOnly = true)
    public EventShowResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<EventShowResponse> findAll(Pageable pageable) {
        return eventShowRepository.findAllByDisabledFalse(toSafePageable(pageable)).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<EventShowResponse> findAllByEventId(Long eventId) {
        eventRepository.findByIdAndDisabledFalse(eventId).orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        return eventShowRepository.findByEvent_Id(eventId).stream()
                .filter(s -> !s.isDisabled())
                .sorted(Comparator.comparing(EventShow::getStartTime).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EventShowResponse update(Long id, EventShowUpsertRequest request) {
        EventShow show = getEntity(id);
        apply(request, show);
        return toResponse(eventShowRepository.save(show));
    }

    @Transactional
    public void delete(Long id) {
        EventShow show = eventShowRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("EventShow", id));
        show.setDisabled(true);
        show.setUpdatedAt(Instant.now());
        eventShowRepository.save(show);
    }

    private EventShow getEntity(Long id) {
        return eventShowRepository.findByIdAndDisabledFalse(id).orElseThrow(() -> new ResourceNotFoundException("EventShow", id));
    }

    private void apply(EventShowUpsertRequest request, EventShow show) {
        Event event = eventRepository.findByIdAndDisabledFalse(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", request.getEventId()));
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue", request.getVenueId()));
        show.setEvent(event);
        show.setVenue(venue);
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getEndTime());
    }

    private EventShowResponse toResponse(EventShow show) {
        return EventShowResponse.builder()
                .id(show.getId())
                .eventId(show.getEvent().getId())
                .venueId(show.getVenue().getId())
                .startTime(show.getStartTime())
                .endTime(show.getEndTime())
                .createdAt(show.getCreatedAt())
                .updatedAt(show.getUpdatedAt())
                .isDisabled(show.isDisabled())
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