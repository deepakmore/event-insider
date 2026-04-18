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


import com.myproject.insider.dto.response.ShowSeatPricingResponse;
import com.myproject.insider.dto.request.ShowSeatPricingUpsertRequest;
import com.myproject.insider.service.ShowSeatPricingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST API for {@link com.myproject.insider.entity.ShowSeatPricing} (table {@code show_seat_pricing}:
 * price per event seat category for a given show).
 */
@RestController
@RequestMapping("/api/v1/show-seat-pricings")
@RequiredArgsConstructor
public class ShowSeatPricingController {

    private final ShowSeatPricingService showSeatPricingService;

    @PostMapping

    public ResponseEntity<ShowSeatPricingResponse> create(@Valid @RequestBody ShowSeatPricingUpsertRequest body,
                                                          UriComponentsBuilder uriBuilder) {
        ShowSeatPricingResponse created = showSeatPricingService.create(body);
        URI location = uriBuilder.replacePath("/api/v1/show-seat-pricings/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<ShowSeatPricingResponse>> list(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(showSeatPricingService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShowSeatPricingResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(showSeatPricingService.findById(id));
    }

    @PutMapping("/{id}")

    public ResponseEntity<ShowSeatPricingResponse> update(@PathVariable Long id,
                                                          @Valid @RequestBody ShowSeatPricingUpsertRequest body) {
        return ResponseEntity.ok(showSeatPricingService.update(id, body));
    }

    @DeleteMapping("/{id}")

    public ResponseEntity<Void> delete(@PathVariable Long id) {
        showSeatPricingService.delete(id);
        return ResponseEntity.noContent().build();
    }
}