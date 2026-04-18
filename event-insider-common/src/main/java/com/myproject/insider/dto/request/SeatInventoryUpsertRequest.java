package com.myproject.insider.dto.request;

import com.myproject.insider.enums.SeatInventoryStatus;

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
public class SeatInventoryUpsertRequest {

    @NotNull
    private Long showId;

    @NotNull
    private Long seatId;

    @NotNull
    private SeatInventoryStatus status;

    private Long bookingId;
}