package com.myproject.insider.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myproject.insider.entity.PaymentWebhookReceipt;

public interface PaymentWebhookReceiptRepository extends JpaRepository<PaymentWebhookReceipt, Long> {

    boolean existsByProviderAndExternalEventId(String provider, String externalEventId);

    Optional<PaymentWebhookReceipt> findByProviderAndExternalEventId(String provider, String externalEventId);
}