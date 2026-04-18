package com.myproject.insider.dto.response;

import java.math.BigDecimal;

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
public class ShowSeatPricingResponse {

    private Long id;
    private Long showId;
    private Long eventCategoryId;
    private BigDecimal basePrice;
}