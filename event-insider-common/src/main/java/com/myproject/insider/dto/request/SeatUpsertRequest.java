package com.myproject.insider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class SeatUpsertRequest {

    @NotNull
    private Long showId;

    @NotBlank
    @Size(max = 10)
    private String seatNumber;

    @Size(max = 5)
    private String rowLabel;

    private Integer columnNumber;

    private Float xCoordinate;

    private Float yCoordinate;
}