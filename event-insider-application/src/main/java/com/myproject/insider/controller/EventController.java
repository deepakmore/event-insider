package com.myproject.insider.controller;

import java.net.URI;
import java.util.List;

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
import com.myproject.insider.dto.response.EventResponse;
import com.myproject.insider.dto.response.EventSeatCategoryResponse;
import com.myproject.insider.dto.response.EventShowResponse;
import com.myproject.insider.dto.request.EventUpsertRequest;
import com.myproject.insider.service.EventSeatCategoryService;
import com.myproject.insider.service.EventService;
import com.myproject.insider.service.EventShowService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventShowService eventShowService;
    private final EventSeatCategoryService eventSeatCategoryService;

    @PostMapping

    public ResponseEntity<EventResponse> create(@Valid @RequestBody EventUpsertRequest body, UriComponentsBuilder uriBuilder) {
        EventResponse created = eventService.create(body);
        URI location = uriBuilder.replacePath("/api/v1/events/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> list(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(eventService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.findById(id));
    }

    @GetMapping("/{eventId}/shows")
    public ResponseEntity<List<EventShowResponse>> listShowsForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventShowService.findAllByEventId(eventId));
    }

    @GetMapping("/{eventId}/seat-categories")
    public ResponseEntity<List<EventSeatCategoryResponse>> listSeatCategoriesForEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventSeatCategoryService.findAllByEventId(eventId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> update(@PathVariable Long id, @Valid @RequestBody EventUpsertRequest body) {
        return ResponseEntity.ok(eventService.update(id, body));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }
}