-- ========================================
-- Foreign Keys - user.restaurant_id
-- ========================================
ALTER TABLE "user"
    ADD CONSTRAINT fk_user_restaurant
    FOREIGN KEY (restaurant_id)
    REFERENCES restaurant(restaurant_id);
