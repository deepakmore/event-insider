package com.myproject.insider.dto.request;

import jakarta.validation.constraints.NotBlank;
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
public class EventUpsertRequest {

    @NotBlank
    @Size(max = 200)
    private String name;

    @Size(max = 10_000)
    private String description;

    @NotBlank
    @Size(max = 50)
    private String eventType;

    @NotBlank
    @Size(max = 50)
    private String pricingType;
}