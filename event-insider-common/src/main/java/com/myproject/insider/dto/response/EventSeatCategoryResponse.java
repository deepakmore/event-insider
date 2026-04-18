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
public class EventSeatCategoryResponse {

    private Long id;
    private Long eventId;
    private String name;
    private String description;
    private String colorCode;
    private Instant updatedAt;
    private boolean isDisabled;
}