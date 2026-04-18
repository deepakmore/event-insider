package com.myproject.insider.dto.response;

import java.time.Instant;

import com.myproject.insider.enums.SeatInventoryStatus;

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
public class SeatInventoryWithSeatResponse {

    private Long id;
    private Long showId;
    private Long seatId;
    private String seatNumber;
    private String rowLabel;
    private SeatInventoryStatus status;
    private Long bookingId;
    private Integer version;
    private Instant updatedAt;
}