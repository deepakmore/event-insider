package com.myproject.insider.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class BookingRequest {

    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long showId;

    @NotNull
    @Positive
    private Long eventSeatCategoryId;

    @NotEmpty
    private List<@NotNull @Positive Long> seatInventoryIds;

    @Size(max = 100)
    private String cancelReason;

}