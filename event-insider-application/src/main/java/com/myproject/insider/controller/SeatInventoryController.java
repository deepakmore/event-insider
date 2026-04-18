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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;


import com.myproject.insider.dto.response.SeatInventoryResponse;
import com.myproject.insider.dto.response.SeatInventoryWithSeatResponse;
import com.myproject.insider.dto.request.SeatInventoryUpsertRequest;
import com.myproject.insider.enums.SeatInventoryStatus;
import com.myproject.insider.service.SeatInventoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seat-inventories")
@RequiredArgsConstructor
public class SeatInventoryController {

    private final SeatInventoryService seatInventoryService;

    @PostMapping

    public ResponseEntity<SeatInventoryResponse> create(@Valid @RequestBody SeatInventoryUpsertRequest body,
                                                        UriComponentsBuilder uriBuilder) {
        SeatInventoryResponse created = seatInventoryService.create(body);
        URI location = uriBuilder.replacePath("/api/v1/seat-inventories/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<SeatInventoryResponse>> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(seatInventoryService.findAll(pageable));
    }

    @GetMapping("/by-show/{showId}")
    public ResponseEntity<List<SeatInventoryWithSeatResponse>> listForShow(
            @PathVariable Long showId,
            @RequestParam(required = false) SeatInventoryStatus status) {
        return ResponseEntity.ok(seatInventoryService.findForShowCatalog(showId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SeatInventoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(seatInventoryService.findById(id));
    }

    @PutMapping("/{id}")

    public ResponseEntity<SeatInventoryResponse> update(@PathVariable Long id,
                                                        @Valid @RequestBody SeatInventoryUpsertRequest body) {
        return ResponseEntity.ok(seatInventoryService.update(id, body));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        seatInventoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}