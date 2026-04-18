package com.myproject.insider.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


import com.myproject.insider.dto.response.EventShowResponse;
import com.myproject.insider.dto.response.ShowSeatPricingResponse;
import com.myproject.insider.dto.request.EventShowUpsertRequest;
import com.myproject.insider.service.EventShowService;
import com.myproject.insider.service.ShowSeatPricingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/event-shows")
@RequiredArgsConstructor
public class EventShowController {

    private final EventShowService eventShowService;
    private final ShowSeatPricingService showSeatPricingService;

    @PostMapping

    public ResponseEntity<EventShowResponse> create(@Valid @RequestBody EventShowUpsertRequest body,
                                                    UriComponentsBuilder uriBuilder) {
        EventShowResponse created = eventShowService.create(body);
        URI location = uriBuilder.replacePath("/api/v1/event-shows/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<EventShowResponse>> list(
            @PageableDefault(size = 20, sort = "startTime", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eventShowService.findAll(pageable));
    }

    @GetMapping("/{showId}/seat-categories/{categoryId}/seat-pricing")
    public ResponseEntity<ShowSeatPricingResponse> getSeatPricingForCategory(
            @PathVariable Long showId,
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(showSeatPricingService.findByShowIdAndCategoryId(showId, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventShowResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventShowService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventShowResponse> update(@PathVariable Long id, @Valid @RequestBody EventShowUpsertRequest body) {
        return ResponseEntity.ok(eventShowService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventShowService.delete(id);
        return ResponseEntity.noContent().build();
    }
}