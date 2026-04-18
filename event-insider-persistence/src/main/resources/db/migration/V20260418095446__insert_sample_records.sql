INSERT INTO users (name, email, mobile_number, password_hash)
VALUES
('Deepak', 'deepakmore@outlook.com', '+919420390095', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31my');

INSERT INTO venue (name, city, address, venue_type)
VALUES
('Mahalaxmi Lawns', 'Pune', 'Nagar Road,Pune (MH) Pin No. 411014', 'LARGE');

INSERT INTO event (name, description, event_type, pricing_type)
VALUES
('Arijit Singh Live Concert', 'Live concert by Arijit Singh', 'CONCERT', 'CATEGORY');

INSERT INTO event_seat_category (event_id, name, description, color_code)
VALUES
(1, 'BRONZE', 'Basic seating', '#CD7F32'),
(1, 'SILVER', 'Better view', '#C0C0C0'),
(1, 'GOLD', 'Good seating', '#FFD700'),
(1, 'PLATINUM', 'Premium seating', '#E5E4E2'),
(1, 'DIAMOND', 'Luxury seating', '#B9F2FF'),
(1, 'VIP', 'Exclusive seating', '#FF5733');

INSERT INTO event_show (event_id, venue_id, start_time, end_time)
VALUES
(1, 1, '2026-06-01 18:00:00', '2026-06-01 22:00:00');

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(1, 'A1', 'A', 1, 1, 1),
(1, 'A2', 'A', 2, 2, 1),
(1, 'A3', 'A', 3, 3, 1);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(1, 'B1', 'B', 1, 1, 2),
(1, 'B2', 'B', 2, 2, 2),
(1, 'B3', 'B', 3, 3, 2);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(1, 'C1', 'C', 1, 1, 3),
(1, 'C2', 'C', 2, 2, 3),
(1, 'C3', 'C', 3, 3, 3);

INSERT INTO seat_inventory (show_id, seat_id, status)
SELECT 1, id, 'AVAILABLE'
FROM seat
WHERE show_id = 1;

INSERT INTO show_seat_pricing (show_id, event_category_id, base_price)
VALUES
(1, 1, 500),
(1, 2, 800),
(1, 3, 1000),
(1, 4, 1500),
(1, 5, 2500),
(1, 6, 4000);

-- Bas kar Bassi by Bassi (CONCERT) — Comedy Club tour stops
INSERT INTO venue (name, city, address, venue_type)
VALUES
('Comedy Club', 'Pune', 'Pune City, Pune (MH)', 'SMALL'),
('Comedy Club', 'Mumbai', 'Mumbai City, Mumbai (MH)', 'SMALL'),
('Comedy Club', 'Nagpur', 'Nagpur City, Nagpur (MH)', 'SMALL'),
('Comedy Club', 'Chandigarh', 'Chandigarh City, Chandigarh (CH)', 'SMALL');

INSERT INTO event (name, description, event_type, pricing_type)
VALUES
('Bas kar Bassi by Bassi', 'Stand-up show Bas kar Bassi by Bassi', 'CONCERT', 'CATEGORY');

INSERT INTO event_seat_category (event_id, name, description, color_code)
VALUES
(2, 'BRONZE', 'Basic seating', '#CD7F32'),
(2, 'SILVER', 'Better view', '#C0C0C0'),
(2, 'GOLD', 'Good seating', '#FFD700'),
(2, 'VIP', 'Exclusive seating', '#FF5733');

INSERT INTO event_show (event_id, venue_id, start_time, end_time)
VALUES
(2, 2, '2026-04-17 21:00:00', '2026-04-17 23:00:00'),
(2, 3, '2026-04-18 21:00:00', '2026-04-18 23:00:00'),
(2, 4, '2026-04-19 21:00:00', '2026-04-19 23:00:00'),
(2, 5, '2026-04-20 21:00:00', '2026-04-20 23:00:00');

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(2, 'A1', 'A', 1, 1, 1),
(2, 'A2', 'A', 2, 2, 1),
(2, 'A3', 'A', 3, 3, 1);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(2, 'B1', 'B', 1, 1, 2),
(2, 'B2', 'B', 2, 2, 2),
(2, 'B3', 'B', 3, 3, 2);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(2, 'C1', 'C', 1, 1, 3),
(2, 'C2', 'C', 2, 2, 3),
(2, 'C3', 'C', 3, 3, 3);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(3, 'A1', 'A', 1, 1, 1),
(3, 'A2', 'A', 2, 2, 1),
(3, 'A3', 'A', 3, 3, 1);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(3, 'B1', 'B', 1, 1, 2),
(3, 'B2', 'B', 2, 2, 2),
(3, 'B3', 'B', 3, 3, 2);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(3, 'C1', 'C', 1, 1, 3),
(3, 'C2', 'C', 2, 2, 3),
(3, 'C3', 'C', 3, 3, 3);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(4, 'A1', 'A', 1, 1, 1),
(4, 'A2', 'A', 2, 2, 1),
(4, 'A3', 'A', 3, 3, 1);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(4, 'B1', 'B', 1, 1, 2),
(4, 'B2', 'B', 2, 2, 2),
(4, 'B3', 'B', 3, 3, 2);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(4, 'C1', 'C', 1, 1, 3),
(4, 'C2', 'C', 2, 2, 3),
(4, 'C3', 'C', 3, 3, 3);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(5, 'A1', 'A', 1, 1, 1),
(5, 'A2', 'A', 2, 2, 1),
(5, 'A3', 'A', 3, 3, 1);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(5, 'B1', 'B', 1, 1, 2),
(5, 'B2', 'B', 2, 2, 2),
(5, 'B3', 'B', 3, 3, 2);

INSERT INTO seat (show_id, seat_number, row_label, column_number, x_coordinate, y_coordinate)
VALUES
(5, 'C1', 'C', 1, 1, 3),
(5, 'C2', 'C', 2, 2, 3),
(5, 'C3', 'C', 3, 3, 3);

INSERT INTO seat_inventory (show_id, seat_id, status)
SELECT 2, id, 'AVAILABLE'
FROM seat
WHERE show_id = 2;

INSERT INTO seat_inventory (show_id, seat_id, status)
SELECT 3, id, 'AVAILABLE'
FROM seat
WHERE show_id = 3;

INSERT INTO seat_inventory (show_id, seat_id, status)
SELECT 4, id, 'AVAILABLE'
FROM seat
WHERE show_id = 4;

INSERT INTO seat_inventory (show_id, seat_id, status)
SELECT 5, id, 'AVAILABLE'
FROM seat
WHERE show_id = 5;

INSERT INTO show_seat_pricing (show_id, event_category_id, base_price)
VALUES
(2, 7, 500),
(2, 8, 800),
(2, 9, 1000),
(2, 10, 1500),
(3, 7, 500),
(3, 8, 800),
(3, 9, 1000),
(3, 10, 1500),
(4, 7, 500),
(4, 8, 800),
(4, 9, 1000),
(4, 10, 1500),
(5, 7, 500),
(5, 8, 800),
(5, 9, 1000),
(5, 10, 1500);