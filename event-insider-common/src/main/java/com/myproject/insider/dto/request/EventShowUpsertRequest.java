package com.myproject.insider.dto.request;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
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
public class EventShowUpsertRequest {

    @NotNull
    private Long eventId;

    @NotNull
    private Long venueId;

    @NotNull
    private Instant startTime;

    private Instant endTime;
}