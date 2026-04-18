package com.myproject.insider.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
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
public class ShowSeatPricingUpsertRequest {

    @NotNull
    private Long showId;

    @NotNull
    private Long eventCategoryId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal basePrice;
}