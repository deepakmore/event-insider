package com.myproject.insider.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.myproject.insider.dto.request.BookingRequest;
import com.myproject.insider.dto.response.BookingResponse;
import com.myproject.insider.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest body,
                                                  UriComponentsBuilder uriBuilder) {
        BookingResponse created = bookingService.create(body);
        URI location = uriBuilder.replacePath("/api/v1/bookings/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getById(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(bookingService.findForUser(id, userId));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @RequestParam Long userId, @RequestBody(required = false) BookingRequest request) {
        bookingService.cancelByUser(id, userId, request);
        return ResponseEntity.noContent().build();
    }
}