CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100),
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       mobile_number VARCHAR(20) NOT NULL UNIQUE,
                       created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE venue (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(150) NOT NULL,
                       city VARCHAR(100),
                       address TEXT,
                       venue_type VARCHAR(50),
                       created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE event (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(200) NOT NULL,
                       description TEXT,
                       event_type VARCHAR(50) NOT NULL,     -- CONCERT
                       pricing_type VARCHAR(50) NOT NULL,   -- CATEGORY
                       created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE event_seat_category (
                                     id BIGSERIAL PRIMARY KEY,
                                     event_id BIGINT NOT NULL REFERENCES event(id) ON DELETE CASCADE,
                                     name VARCHAR(50) NOT NULL, -- VIP, Premium, Regular
                                     description TEXT,
                                     color_code VARCHAR(20),

                                     UNIQUE(event_id, name)
);

CREATE TABLE event_show (
                            id BIGSERIAL PRIMARY KEY,
                            event_id BIGINT NOT NULL REFERENCES event(id) ON DELETE CASCADE,
                            venue_id BIGINT NOT NULL REFERENCES venue(id) ON DELETE CASCADE,

                            start_time TIMESTAMP NOT NULL,
                            end_time TIMESTAMP,

                            created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE seat (
                      id BIGSERIAL PRIMARY KEY,
                      show_id BIGINT NOT NULL REFERENCES event_show(id) ON DELETE CASCADE,

                      seat_number VARCHAR(10) NOT NULL,
                      row_label VARCHAR(5),
                      column_number INT,

                      x_coordinate FLOAT,
                      y_coordinate FLOAT,

                      created_at TIMESTAMP DEFAULT NOW(),

                      UNIQUE(show_id, seat_number)
);

CREATE TABLE seat_inventory (
                                id BIGSERIAL PRIMARY KEY,
                                show_id BIGINT NOT NULL REFERENCES event_show(id) ON DELETE CASCADE,
                                seat_id BIGINT NOT NULL REFERENCES seat(id) ON DELETE CASCADE,

                                status VARCHAR(20) NOT NULL CHECK (status IN ('AVAILABLE', 'HELD', 'BOOKED')),

                                hold_id BIGINT,
                                booking_id BIGINT,

                                version INT DEFAULT 0,
                                updated_at TIMESTAMP DEFAULT NOW(),

                                UNIQUE(show_id, seat_id)
);

CREATE TABLE seat_hold (
                           id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT NOT NULL REFERENCES users(id),
                           show_id BIGINT NOT NULL REFERENCES event_show(id),

                           status VARCHAR(20) NOT NULL CHECK (status IN ('ACTIVE', 'EXPIRED', 'CONFIRMED')),

                           expires_at TIMESTAMP NOT NULL,
                           created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE seat_hold_seat (
                                hold_id BIGINT NOT NULL REFERENCES seat_hold(id) ON DELETE CASCADE,
                                seat_inventory_id BIGINT NOT NULL REFERENCES seat_inventory(id) ON DELETE CASCADE,

                                PRIMARY KEY (hold_id, seat_inventory_id)
);

CREATE TABLE booking (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL REFERENCES users(id),
                         show_id BIGINT NOT NULL REFERENCES event_show(id),
                         hold_id BIGINT REFERENCES seat_hold(id),

                         status VARCHAR(20) NOT NULL CHECK (status IN ('CONFIRMED', 'CANCELLED')),

                         created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE booking_seat (
                              booking_id BIGINT NOT NULL REFERENCES booking(id) ON DELETE CASCADE,
                              seat_inventory_id BIGINT NOT NULL REFERENCES seat_inventory(id) ON DELETE CASCADE,

                              price NUMERIC(10,2) NOT NULL,

                              PRIMARY KEY (booking_id, seat_inventory_id)
);

CREATE TABLE show_seat_pricing (
                                   id BIGSERIAL PRIMARY KEY,
                                   show_id BIGINT NOT NULL REFERENCES event_show(id) ON DELETE CASCADE,
                                   event_category_id BIGINT NOT NULL REFERENCES event_seat_category(id),

                                   base_price NUMERIC(10,2) NOT NULL,

                                   UNIQUE(show_id, event_category_id)
);