package com.myproject.insider.dto.response;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

    private Long id;
    private String name;
    private String description;
    private String eventType;
    private String pricingType;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDisabled;
}