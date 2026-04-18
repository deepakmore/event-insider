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


import com.myproject.insider.dto.response.SeatResponse;
import com.myproject.insider.dto.request.SeatUpsertRequest;
import com.myproject.insider.service.SeatService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

    private final SeatService seatService;

    @PostMapping

    public ResponseEntity<SeatResponse> create(@Valid @RequestBody SeatUpsertRequest body,
                                               UriComponentsBuilder uriBuilder) {
        SeatResponse created = seatService.create(body);
        URI location = uriBuilder.replacePath("/api/v1/seats/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<SeatResponse>> list(
            @PageableDefault(size = 20, sort = "seatNumber", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(seatService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(seatService.findById(id));
    }

    @PutMapping("/{id}")

    public ResponseEntity<SeatResponse> update(@PathVariable Long id, @Valid @RequestBody SeatUpsertRequest body) {
        return ResponseEntity.ok(seatService.update(id, body));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatService.delete(id);
        return ResponseEntity.noContent().build();
    }
}