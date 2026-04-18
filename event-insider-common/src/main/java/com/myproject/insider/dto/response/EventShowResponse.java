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
public class EventShowResponse {

    private Long id;
    private Long eventId;
    private Long venueId;
    private Instant startTime;
    private Instant endTime;
    private Instant createdAt;
    private Instant updatedAt;
    private boolean isDisabled;
}