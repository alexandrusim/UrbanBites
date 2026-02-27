-- ========================================
-- VIEWS
-- ========================================

-- View pentru rezervări active
CREATE OR REPLACE VIEW active_reservations AS
SELECT
    r.reservation_id,
    r.confirmation_code,
    u.first_name || ' ' || u.last_name AS guest_name,
    u.email AS guest_email,
    u.phone_number AS guest_phone,
    rest.name AS restaurant_name,
    t.table_number,
    t.capacity,
    r.reservation_date,
    r.reservation_time,
    r.number_of_guests,
    r.status,
    r.special_requests,
    r.created_at
FROM reservation r
JOIN "user" u ON r.user_id = u.user_id
JOIN restaurant rest ON r.restaurant_id = rest.restaurant_id
JOIN "table" t ON r.table_id = t.table_id
WHERE r.status IN ('CONFIRMED', 'PENDING')
  AND r.reservation_date >= CURRENT_DATE
ORDER BY r.reservation_date, r.reservation_time;

-- View pentru statistici restaurant
CREATE OR REPLACE VIEW restaurant_statistics AS
SELECT
    r.restaurant_id,
    r.name,
    COUNT(DISTINCT res.reservation_id) AS total_reservations,
    COUNT(DISTINCT CASE
        WHEN res.status = 'COMPLETED'
        THEN res.reservation_id
    END) AS completed_reservations,
    COUNT(DISTINCT CASE
        WHEN res.status = 'CANCELLED'
        THEN res.reservation_id
    END) AS cancelled_reservations,
    COUNT(DISTINCT CASE
        WHEN res.status = 'NO_SHOW'
        THEN res.reservation_id
    END) AS no_show_count,
    COALESCE(AVG(f.rating), 0)::DECIMAL(3,2) AS avg_rating,
    COUNT(DISTINCT f.feedback_id) AS total_reviews,
    COALESCE(SUM(p.amount), 0)::DECIMAL(10,2) AS total_revenue
FROM restaurant r
LEFT JOIN reservation res ON r.restaurant_id = res.restaurant_id
LEFT JOIN feedback f ON r.restaurant_id = f.restaurant_id
    AND f.is_visible = TRUE
LEFT JOIN payment p ON res.reservation_id = p.reservation_id
    AND p.payment_status = 'COMPLETED'
GROUP BY r.restaurant_id, r.name;

-- View pentru meniu complet
CREATE OR REPLACE VIEW full_menu_view AS
SELECT
    r.restaurant_id,
    r.name AS restaurant_name,
    m.menu_id,
    m.name AS menu_name,
    m.description AS menu_description,
    mi.item_id,
    mi.name AS item_name,
    mi.description AS item_description,
    mi.price,
    mi.category,
    mi.image_url,
    mi.ingredients,
    mi.allergens,
    mi.calories,
    mi.is_vegetarian,
    mi.is_vegan,
    mi.is_gluten_free,
    mi.is_available,
    mi.display_order
FROM restaurant r
JOIN menu m ON r.restaurant_id = m.restaurant_id
JOIN menu_item mi ON m.menu_id = mi.menu_id
WHERE m.is_active = TRUE
  AND mi.is_available = TRUE
ORDER BY r.restaurant_id, m.menu_id, mi.display_order;
