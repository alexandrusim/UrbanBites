-- Activare extensii
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ========================================
-- Tabel: user
-- ========================================
CREATE TABLE "user" (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL
        CHECK (role IN ('CLIENT', 'ADMIN_RESTAURANT', 'SYSTEM_ADMIN')),
    restaurant_id INTEGER,
    is_active BOOLEAN DEFAULT TRUE,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    last_login_at TIMESTAMP
);

CREATE INDEX idx_user_email ON "user"(email);
CREATE INDEX idx_user_restaurant ON "user"(restaurant_id);

-- ========================================
-- Tabel: restaurant
-- ========================================
CREATE TABLE restaurant (
    restaurant_id SERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    address VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'România',
    phone_number VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    website VARCHAR(255),
    logo_url VARCHAR(500),
    cover_image_url VARCHAR(500),
    svg_layout TEXT,
    cuisine_type VARCHAR(100),
    price_range INTEGER CHECK (price_range BETWEEN 1 AND 4),
    capacity INTEGER CHECK (capacity > 0),
    rating_average DECIMAL(3,2) DEFAULT 0.00
        CHECK (rating_average BETWEEN 0 AND 5),
    rating_count INTEGER DEFAULT 0,
    opening_hours JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_restaurant_city ON restaurant(city);
CREATE INDEX idx_restaurant_cuisine ON restaurant(cuisine_type);
CREATE INDEX idx_restaurant_rating ON restaurant(rating_average DESC);

-- ========================================
-- Tabel: table
-- ========================================
CREATE TABLE "table" (
    table_id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL
        REFERENCES restaurant(restaurant_id) ON DELETE CASCADE,
    table_number VARCHAR(10) NOT NULL,
    capacity INTEGER NOT NULL CHECK (capacity > 0 AND capacity <= 20),
    location VARCHAR(50),
    is_available BOOLEAN DEFAULT TRUE,
    position_x DECIMAL(10,2),
    position_y DECIMAL(10,2),
    shape VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    UNIQUE(restaurant_id, table_number)
);

CREATE INDEX idx_table_restaurant_available
    ON "table"(restaurant_id, is_available);
CREATE INDEX idx_table_capacity ON "table"(capacity);

-- ========================================
-- Tabel: menu
-- ========================================
CREATE TABLE menu (
    menu_id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL
        REFERENCES restaurant(restaurant_id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    available_from TIME,
    available_to TIME,
    valid_days VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_menu_restaurant_active
    ON menu(restaurant_id, is_active);

-- ========================================
-- Tabel: menu_item
-- ========================================
CREATE TABLE menu_item (
    item_id SERIAL PRIMARY KEY,
    menu_id INTEGER NOT NULL
        REFERENCES menu(menu_id) ON DELETE CASCADE,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    category VARCHAR(50),
    image_url VARCHAR(500),
    ingredients TEXT,
    allergens TEXT,
    calories INTEGER CHECK (calories >= 0),
    preparation_time INTEGER CHECK (preparation_time >= 0),
    is_vegetarian BOOLEAN DEFAULT FALSE,
    is_vegan BOOLEAN DEFAULT FALSE,
    is_gluten_free BOOLEAN DEFAULT FALSE,
    is_available BOOLEAN DEFAULT TRUE,
    display_order INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_menu_item_menu_available
    ON menu_item(menu_id, is_available);
CREATE INDEX idx_menu_item_category ON menu_item(category);
CREATE INDEX idx_menu_item_order ON menu_item(display_order);

-- ========================================
-- Tabel: reservation
-- ========================================
CREATE TABLE reservation (
    reservation_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(user_id),
    restaurant_id INTEGER NOT NULL
        REFERENCES restaurant(restaurant_id),
    table_id INTEGER NOT NULL REFERENCES "table"(table_id),
    reservation_date DATE NOT NULL,
    reservation_time TIME NOT NULL,
    duration_minutes INTEGER DEFAULT 120 CHECK (duration_minutes > 0),
    number_of_guests INTEGER NOT NULL CHECK (number_of_guests > 0),
    status VARCHAR(20) NOT NULL
        CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED',
                          'COMPLETED', 'NO_SHOW')),
    special_requests TEXT,
    confirmation_code VARCHAR(10) UNIQUE,
    cancelled_at TIMESTAMP,
    cancellation_reason TEXT,
    checked_in_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_reservation_table_date_time
    ON reservation(table_id, reservation_date, reservation_time);
CREATE INDEX idx_reservation_user_status
    ON reservation(user_id, status);
CREATE INDEX idx_reservation_restaurant_date
    ON reservation(restaurant_id, reservation_date);
CREATE INDEX idx_reservation_confirmation
    ON reservation(confirmation_code);

-- ========================================
-- Tabel: payment
-- ========================================
CREATE TABLE payment (
    payment_id SERIAL PRIMARY KEY,
    reservation_id INTEGER NOT NULL
        REFERENCES reservation(reservation_id),
    user_id INTEGER NOT NULL REFERENCES "user"(user_id),
    amount DECIMAL(10,2) NOT NULL CHECK (amount >= 0),
    currency VARCHAR(3) DEFAULT 'RON',
    payment_method VARCHAR(50) NOT NULL
        CHECK (payment_method IN ('CARD', 'CASH', 'ONLINE', 'MOBILE_APP')),
    payment_status VARCHAR(20) NOT NULL
        CHECK (payment_status IN ('PENDING', 'COMPLETED',
                                   'FAILED', 'REFUNDED')),
    transaction_id VARCHAR(100),
    payment_provider VARCHAR(50),
    paid_at TIMESTAMP,
    refunded_at TIMESTAMP,
    refund_amount DECIMAL(10,2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_payment_reservation ON payment(reservation_id);
CREATE INDEX idx_payment_user_status ON payment(user_id, payment_status);
CREATE INDEX idx_payment_transaction ON payment(transaction_id);

-- ========================================
-- Tabel: feedback
-- ========================================
CREATE TABLE feedback (
    feedback_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(user_id),
    restaurant_id INTEGER NOT NULL
        REFERENCES restaurant(restaurant_id),
    reservation_id INTEGER REFERENCES reservation(reservation_id),
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    food_rating INTEGER CHECK (food_rating BETWEEN 1 AND 5),
    service_rating INTEGER CHECK (service_rating BETWEEN 1 AND 5),
    ambiance_rating INTEGER CHECK (ambiance_rating BETWEEN 1 AND 5),
    value_rating INTEGER CHECK (value_rating BETWEEN 1 AND 5),
    is_visible BOOLEAN DEFAULT TRUE,
    admin_response TEXT,
    responded_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_feedback_restaurant_visible
    ON feedback(restaurant_id, is_visible);
CREATE INDEX idx_feedback_user ON feedback(user_id);
CREATE INDEX idx_feedback_rating ON feedback(rating);

-- ========================================
-- Tabel: notification
-- ========================================
CREATE TABLE notification (
    notification_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES "user"(user_id),
    type VARCHAR(50) NOT NULL
        CHECK (type IN ('EMAIL', 'PUSH', 'SMS', 'IN_APP')),
    title VARCHAR(200),
    message TEXT NOT NULL,
    related_type VARCHAR(50),
    related_id INTEGER,
    status VARCHAR(20) NOT NULL
        CHECK (status IN ('PENDING', 'SENT', 'DELIVERED',
                          'FAILED', 'READ')),
    sent_at TIMESTAMP,
    read_at TIMESTAMP,
    error_message TEXT,
    retry_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_notification_user_status
    ON notification(user_id, status);
CREATE INDEX idx_notification_user_read
    ON notification(user_id, read_at);
CREATE INDEX idx_notification_created ON notification(created_at);

-- ========================================
-- Tabel: contact_message
-- ========================================
CREATE TABLE contact_message (
    message_id SERIAL PRIMARY KEY,
    restaurant_id INTEGER NOT NULL
        REFERENCES restaurant(restaurant_id),
    user_id INTEGER REFERENCES "user"(user_id),
    sender_name VARCHAR(100) NOT NULL,
    sender_email VARCHAR(255) NOT NULL,
    sender_phone VARCHAR(20),
    subject VARCHAR(200),
    message TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'NEW'
        CHECK (status IN ('NEW', 'READ', 'REPLIED', 'ARCHIVED')),
    admin_reply TEXT,
    replied_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_contact_restaurant_status
    ON contact_message(restaurant_id, status);
CREATE INDEX idx_contact_created ON contact_message(created_at);
