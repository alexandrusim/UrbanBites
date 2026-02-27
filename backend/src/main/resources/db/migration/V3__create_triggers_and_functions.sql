-- ========================================
-- TRIGGERS AND FUNCTIONS
-- ========================================

-- Trigger pentru actualizare timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicare trigger pe toate tabelele cu updated_at
CREATE TRIGGER update_user_updated_at
    BEFORE UPDATE ON "user"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_restaurant_updated_at
    BEFORE UPDATE ON restaurant
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_table_updated_at
    BEFORE UPDATE ON "table"
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_menu_updated_at
    BEFORE UPDATE ON menu
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_menu_item_updated_at
    BEFORE UPDATE ON menu_item
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reservation_updated_at
    BEFORE UPDATE ON reservation
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_payment_updated_at
    BEFORE UPDATE ON payment
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_feedback_updated_at
    BEFORE UPDATE ON feedback
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_notification_updated_at
    BEFORE UPDATE ON notification
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Trigger pentru actualizare rating restaurant
CREATE OR REPLACE FUNCTION update_restaurant_rating()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE restaurant
    SET rating_average = (
        SELECT AVG(rating)::DECIMAL(3,2)
        FROM feedback
        WHERE restaurant_id = NEW.restaurant_id
          AND is_visible = TRUE
    ),
    rating_count = (
        SELECT COUNT(*)
        FROM feedback
        WHERE restaurant_id = NEW.restaurant_id
          AND is_visible = TRUE
    )
    WHERE restaurant_id = NEW.restaurant_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_restaurant_rating_trigger
    AFTER INSERT OR UPDATE ON feedback
    FOR EACH ROW
    WHEN (NEW.is_visible = TRUE)
    EXECUTE FUNCTION update_restaurant_rating();

-- Funcție pentru generare cod confirmare
CREATE OR REPLACE FUNCTION generate_confirmation_code()
RETURNS TEXT AS $$
DECLARE
    v_code TEXT;
    v_exists BOOLEAN;
BEGIN
    LOOP
        -- Generare cod aleatoriu de 8 caractere
        v_code := UPPER(SUBSTRING(MD5(RANDOM()::TEXT) FROM 1 FOR 8));

        -- Verificare unicitate
        SELECT EXISTS(
            SELECT 1 FROM reservation
            WHERE confirmation_code = v_code
        ) INTO v_exists;

        EXIT WHEN NOT v_exists;
    END LOOP;

    RETURN v_code;
END;
$$ LANGUAGE plpgsql;

-- Trigger pentru auto-generare cod
CREATE OR REPLACE FUNCTION set_confirmation_code()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.confirmation_code IS NULL THEN
        NEW.confirmation_code := generate_confirmation_code();
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER set_reservation_confirmation_code
    BEFORE INSERT ON reservation
    FOR EACH ROW
    EXECUTE FUNCTION set_confirmation_code();

-- Funcție pentru verificare disponibilitate masă
CREATE OR REPLACE FUNCTION check_table_availability(
    p_table_id INTEGER,
    p_reservation_date DATE,
    p_reservation_time TIME,
    p_duration_minutes INTEGER
) RETURNS BOOLEAN AS $$
DECLARE
    v_conflict_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO v_conflict_count
    FROM reservation
    WHERE table_id = p_table_id
      AND reservation_date = p_reservation_date
      AND status IN ('CONFIRMED', 'PENDING')
      AND (
          -- Overlap logic
          (reservation_time, reservation_time +
           (duration_minutes || ' minutes')::INTERVAL)
          OVERLAPS
          (p_reservation_time, p_reservation_time +
           (p_duration_minutes || ' minutes')::INTERVAL)
      );

    RETURN v_conflict_count = 0;
END;
$$ LANGUAGE plpgsql;
