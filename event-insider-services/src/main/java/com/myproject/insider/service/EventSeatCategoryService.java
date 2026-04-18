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

import com.myproject.insider.dto.response.EventSeatCategoryResponse;
import com.myproject.insider.dto.request.EventSeatCategoryUpsertRequest;
import com.myproject.insider.entity.Event;
import com.myproject.insider.entity.EventSeatCategory;
import com.myproject.insider.exception.ApiBadRequestException;
import com.myproject.insider.exception.ResourceNotFoundException;
import com.myproject.insider.repository.EventRepository;
import com.myproject.insider.repository.EventSeatCategoryRepository;
import com.myproject.insider.repository.ShowSeatPricingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventSeatCategoryService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "name");

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "name");

    private final EventSeatCategoryRepository eventSeatCategoryRepository;
    private final EventRepository eventRepository;
    private final ShowSeatPricingRepository showSeatPricingRepository;

    @Transactional
    public EventSeatCategoryResponse create(EventSeatCategoryUpsertRequest request) {
        EventSeatCategory entity = EventSeatCategory.builder().build();
        apply(request, entity);
        entity.setDisabled(false);
        return toResponse(eventSeatCategoryRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public EventSeatCategoryResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<EventSeatCategoryResponse> findAll(Pageable pageable) {
        return eventSeatCategoryRepository.findAllByDisabledFalse(toSafePageable(pageable)).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<EventSeatCategoryResponse> findAllByEventId(Long eventId) {
        eventRepository.findByIdAndDisabledFalse(eventId).orElseThrow(() -> new ResourceNotFoundException("Event", eventId));
        return eventSeatCategoryRepository.findByEvent_Id(eventId).stream()
                .filter(c -> !c.isDisabled())
                .sorted(Comparator.comparing(EventSeatCategory::getName))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EventSeatCategoryResponse update(Long id, EventSeatCategoryUpsertRequest request) {
        EventSeatCategory entity = getEntity(id);
        if (!entity.getEvent().getId().equals(request.getEventId())
                && showSeatPricingRepository.existsByEventCategory_Id(id)) {
            throw new ApiBadRequestException("Cannot change event when this category is used on show pricing");
        }
        apply(request, entity);
        return toResponse(eventSeatCategoryRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        EventSeatCategory entity = eventSeatCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventSeatCategory", id));
        entity.setDisabled(true);
        entity.setUpdatedAt(Instant.now());
        eventSeatCategoryRepository.save(entity);
    }

    private EventSeatCategory getEntity(Long id) {
        return eventSeatCategoryRepository.findByIdAndDisabledFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("EventSeatCategory", id));
    }

    private void apply(EventSeatCategoryUpsertRequest request, EventSeatCategory entity) {
        Event event = eventRepository.findByIdAndDisabledFalse(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event", request.getEventId()));
        entity.setEvent(event);
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setColorCode(request.getColorCode());
    }

    private EventSeatCategoryResponse toResponse(EventSeatCategory entity) {
        return EventSeatCategoryResponse.builder()
                .id(entity.getId())
                .eventId(entity.getEvent().getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .colorCode(entity.getColorCode())
                .updatedAt(entity.getUpdatedAt())
                .isDisabled(entity.isDisabled())
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