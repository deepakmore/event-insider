package com.myproject.insider.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myproject.insider.dto.request.PaymentWebhookRequest;
import com.myproject.insider.exception.ApiBadRequestException;
import com.myproject.insider.kafka.SlackAlertKafkaTrigger;
import com.myproject.insider.service.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    public static final String WEBHOOK_SECRET_HEADER = "X-Payment-Webhook-Secret";

    private final BookingService bookingService;
    private final SlackAlertKafkaTrigger slackAlertKafkaTrigger;

    @Value("${app.payment.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(WEBHOOK_SECRET_HEADER) String providedSecret,
            @Valid @RequestBody PaymentWebhookRequest body) {
        verifySecret(providedSecret);
        bookingService.confirmPaidFromWebhook(body.getProvider(), body.getExternalEventId(),
                body.getBookingId(), body.getPaymentStatus());
        return ResponseEntity.ok().build();
    }

    private void verifySecret(String providedSecret) {
        if (providedSecret == null) {
            slackAlertKafkaTrigger.publish(
                    "PAYMENT_WEBHOOK_INVALID_SECRET",
                    "P0",
                    "Missing webhook secret header");
            throw new ApiBadRequestException("Missing webhook secret header");
        }
        byte[] expected = webhookSecret.getBytes(StandardCharsets.UTF_8);
        byte[] actual = providedSecret.getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, actual)) {
            slackAlertKafkaTrigger.publish(
                    "PAYMENT_WEBHOOK_INVALID_SECRET",
                    "P0",
                    "Invalid webhook secret");
            throw new ApiBadRequestException("Invalid webhook secret");
        }
    }
}