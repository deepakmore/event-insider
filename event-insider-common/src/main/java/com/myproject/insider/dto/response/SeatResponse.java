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
public class SeatResponse {

    private Long id;
    private Long showId;
    private String seatNumber;
    private String rowLabel;
    private Integer columnNumber;
    private Float xCoordinate;
    private Float yCoordinate;
    private Instant createdAt;
}