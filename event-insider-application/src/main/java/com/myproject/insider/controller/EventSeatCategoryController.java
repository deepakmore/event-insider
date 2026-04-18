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


import com.myproject.insider.dto.response.EventSeatCategoryResponse;
import com.myproject.insider.dto.request.EventSeatCategoryUpsertRequest;
import com.myproject.insider.service.EventSeatCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/event-seat-categories")
@RequiredArgsConstructor
public class EventSeatCategoryController {

    private final EventSeatCategoryService eventSeatCategoryService;

    @PostMapping

    public ResponseEntity<EventSeatCategoryResponse> create(@Valid @RequestBody EventSeatCategoryUpsertRequest body,
                                                            UriComponentsBuilder uriBuilder) {
        EventSeatCategoryResponse created = eventSeatCategoryService.create(body);
        URI location = uriBuilder.replacePath("/api/v1/event-seat-categories/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<EventSeatCategoryResponse>> list(
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(eventSeatCategoryService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventSeatCategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventSeatCategoryService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventSeatCategoryResponse> update(@PathVariable Long id,
                                                            @Valid @RequestBody EventSeatCategoryUpsertRequest body) {
        return ResponseEntity.ok(eventSeatCategoryService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventSeatCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}