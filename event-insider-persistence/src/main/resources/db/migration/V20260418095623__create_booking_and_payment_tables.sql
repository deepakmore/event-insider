ALTER TABLE booking
    ADD COLUMN IF NOT EXISTS hold_expires_at TIMESTAMP;

CREATE INDEX IF NOT EXISTS idx_booking_in_progress_expires
    ON booking (hold_expires_at)
    WHERE status = 'IN_PROGRESS';

CREATE TABLE payment_webhook_receipt (
                                         id BIGSERIAL PRIMARY KEY,
                                         provider VARCHAR(50) NOT NULL,
                                         external_event_id VARCHAR(255) NOT NULL,
                                         booking_id BIGINT NOT NULL REFERENCES booking (id),
                                         created_at TIMESTAMP DEFAULT NOW(),
                                         UNIQUE (provider, external_event_id)
);

CREATE INDEX IF NOT EXISTS idx_payment_webhook_receipt_booking
    ON payment_webhook_receipt (booking_id);