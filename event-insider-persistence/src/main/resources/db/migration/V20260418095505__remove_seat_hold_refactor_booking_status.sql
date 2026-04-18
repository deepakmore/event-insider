-- Seat holds are removed: bookings own seat lines via booking_seat; lifecycle is booking.status only.
DROP INDEX IF EXISTS idx_seat_hold_expiry;

ALTER TABLE booking DROP CONSTRAINT IF EXISTS booking_hold_id_fkey;
ALTER TABLE booking DROP COLUMN IF EXISTS hold_id;

ALTER TABLE seat_inventory DROP COLUMN IF EXISTS hold_id;

DROP TABLE IF EXISTS seat_hold_seat;
DROP TABLE IF EXISTS seat_hold;

-- Migrate legacy status values before tightening CHECK constraint
UPDATE booking SET status = 'COMPLETE' WHERE status = 'CONFIRMED';

ALTER TABLE booking DROP CONSTRAINT IF EXISTS booking_status_check;

ALTER TABLE booking
    ADD CONSTRAINT booking_status_check CHECK (status IN ('IN_PROGRESS', 'COMPLETE', 'CANCELLED'));