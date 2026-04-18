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

import com.myproject.insider.dto.response.EventResponse;
import com.myproject.insider.dto.request.EventUpsertRequest;
import com.myproject.insider.entity.Event;
import com.myproject.insider.exception.ResourceNotFoundException;
import com.myproject.insider.repository.EventRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final Sort DEFAULT_EVENT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    /** JPA property names on {@link Event} that are safe for {@code ORDER BY}. */
    private static final Set<String> ALLOWED_EVENT_SORT_PROPERTIES =
            Set.of("id", "name", "description", "eventType", "pricingType", "createdAt");

    private final EventRepository eventRepository;

    @Transactional
    public EventResponse create(EventUpsertRequest request) {
        Event event = Event.builder()
                .name(request.getName())
                .description(request.getDescription())
                .eventType(request.getEventType())
                .pricingType(request.getPricingType())
                .createdAt(Instant.now())
                .disabled(false)
                .build();
        return toResponse(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    public EventResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<EventResponse> findAll(Pageable pageable) {
        return eventRepository.findAllByDisabledFalse(toSafeEventPageable(pageable)).map(this::toResponse);
    }

    private static Pageable toSafeEventPageable(Pageable pageable) {
        Sort sort = pageable.getSort();
        if (!sort.isSorted()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_EVENT_SORT);
        }
        List<Order> safe = new ArrayList<>();
        for (Order order : sort) {
            String prop = order.getProperty();
            if (prop != null && ALLOWED_EVENT_SORT_PROPERTIES.contains(prop)) {
                safe.add(order);
            }
        }
        if (safe.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), DEFAULT_EVENT_SORT);
        }
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(safe));
    }

    @Transactional
    public EventResponse update(Long id, EventUpsertRequest request) {
        Event event = getEntity(id);
        apply(request, event);
        return toResponse(eventRepository.save(event));
    }

    @Transactional
    public void delete(Long id) {
        Event event = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Event", id));
        event.setDisabled(true);
        event.setUpdatedAt(Instant.now());
        eventRepository.save(event);
    }

    private Event getEntity(Long id) {
        return eventRepository.findByIdAndDisabledFalse(id).orElseThrow(() -> new ResourceNotFoundException("Event", id));
    }

    private void apply(EventUpsertRequest request, Event event) {
        event.setName(request.getName());
        event.setDescription(request.getDescription());
        event.setEventType(request.getEventType());
        event.setPricingType(request.getPricingType());
    }

    private EventResponse toResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .eventType(event.getEventType())
                .pricingType(event.getPricingType())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .isDisabled(event.isDisabled())
                .build();
    }
}