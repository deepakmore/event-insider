package com.myproject.insider.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PaymentWebhookRequest {

    @NotBlank
    private String provider;

    @NotBlank
    private String externalEventId;

    @NotNull
    @Positive
    private Long bookingId;

    @NotBlank
    private String paymentStatus;
}