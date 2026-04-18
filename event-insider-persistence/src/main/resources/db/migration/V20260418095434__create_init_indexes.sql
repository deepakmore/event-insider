-- Seat availability
CREATE INDEX idx_seat_inventory_show ON seat_inventory(show_id);

-- Hold expiry
CREATE INDEX idx_seat_hold_expiry ON seat_hold(expires_at);

-- Show lookup
CREATE INDEX idx_show_event ON event_show(event_id);

-- Pricing lookup
CREATE INDEX idx_pricing_show ON show_seat_pricing(show_id);

-- double booking
CREATE UNIQUE INDEX unique_booked_seat
ON seat_inventory(show_id, seat_id)
WHERE status = 'BOOKED';