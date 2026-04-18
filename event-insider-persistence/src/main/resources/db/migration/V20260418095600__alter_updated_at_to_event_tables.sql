ALTER TABLE event
    ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE event_seat_category
    ADD COLUMN updated_at TIMESTAMP;

ALTER TABLE event_show
    ADD COLUMN updated_at TIMESTAMP;