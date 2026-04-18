package com.myproject.insider.service;

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

import com.myproject.insider.dto.response.ShowSeatPricingResponse;
import com.myproject.insider.dto.request.ShowSeatPricingUpsertRequest;
import com.myproject.insider.entity.EventSeatCategory;
import com.myproject.insider.entity.EventShow;
import com.myproject.insider.entity.ShowSeatPricing;
import com.myproject.insider.exception.ApiBadRequestException;
import com.myproject.insider.exception.ResourceNotFoundException;
import com.myproject.insider.repository.EventSeatCategoryRepository;
import com.myproject.insider.repository.EventShowRepository;
import com.myproject.insider.repository.ShowSeatPricingRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShowSeatPricingService {

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.ASC, "id");

    private static final Set<String> ALLOWED_SORT_PROPERTIES = Set.of("id", "basePrice");

    private final ShowSeatPricingRepository showSeatPricingRepository;
    private final EventShowRepository eventShowRepository;
    private final EventSeatCategoryRepository eventSeatCategoryRepository;

    @Transactional
    public ShowSeatPricingResponse create(ShowSeatPricingUpsertRequest request) {
        ShowSeatPricing entity = ShowSeatPricing.builder().build();
        apply(request, entity);
        return toResponse(showSeatPricingRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public ShowSeatPricingResponse findById(Long id) {
        return toResponse(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Page<ShowSeatPricingResponse> findAll(Pageable pageable) {
        return showSeatPricingRepository.findAll(toSafePageable(pageable)).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ShowSeatPricingResponse findByShowIdAndCategoryId(Long showId, Long categoryId) {
        ShowSeatPricing pricing = showSeatPricingRepository
                .findByEventShow_IdAndEventCategory_Id(showId, categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("ShowSeatPricing", showId + "/" + categoryId));
        EventShow show = pricing.getEventShow();
        if (show.isDisabled()) {
            throw new ResourceNotFoundException("EventShow", showId);
        }
        EventSeatCategory category = pricing.getEventCategory();
        if (category.isDisabled()) {
            throw new ResourceNotFoundException("EventSeatCategory", categoryId);
        }
        assertSameEvent(show, category);
        return toResponse(pricing);
    }

    @Transactional
    public ShowSeatPricingResponse update(Long id, ShowSeatPricingUpsertRequest request) {
        ShowSeatPricing entity = getEntity(id);
        apply(request, entity);
        return toResponse(showSeatPricingRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!showSeatPricingRepository.existsById(id)) {
            throw new ResourceNotFoundException("ShowSeatPricing", id);
        }
        showSeatPricingRepository.deleteById(id);
    }

    private ShowSeatPricing getEntity(Long id) {
        return showSeatPricingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ShowSeatPricing", id));
    }

    private void apply(ShowSeatPricingUpsertRequest request, ShowSeatPricing entity) {
        EventShow show = eventShowRepository.findByIdAndDisabledFalse(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("EventShow", request.getShowId()));
        EventSeatCategory category = eventSeatCategoryRepository.findByIdAndDisabledFalse(request.getEventCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("EventSeatCategory", request.getEventCategoryId()));
        assertSameEvent(show, category);
        entity.setEventShow(show);
        entity.setEventCategory(category);
        entity.setBasePrice(request.getBasePrice());
    }

    private static void assertSameEvent(EventShow show, EventSeatCategory category) {
        Long showEventId = show.getEvent().getId();
        Long categoryEventId = category.getEvent().getId();
        if (!Objects.equals(showEventId, categoryEventId)) {
            throw new ApiBadRequestException("Event seat category must belong to the same event as the show");
        }
    }

    private ShowSeatPricingResponse toResponse(ShowSeatPricing entity) {
        return ShowSeatPricingResponse.builder()
                .id(entity.getId())
                .showId(entity.getEventShow().getId())
                .eventCategoryId(entity.getEventCategory().getId())
                .basePrice(entity.getBasePrice())
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