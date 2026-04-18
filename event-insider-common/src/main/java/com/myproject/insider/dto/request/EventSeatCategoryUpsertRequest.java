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
public class EventSeatCategoryUpsertRequest {

    @NotNull
    private Long eventId;

    @NotBlank
    @Size(max = 50)
    private String name;

    @Size(max = 10_000)
    private String description;

    @Size(max = 20)
    private String colorCode;
}