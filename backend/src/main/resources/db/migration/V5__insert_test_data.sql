-- Inserare date de test pentru restaurante
INSERT INTO restaurant (name, description, address, city, postal_code, country, phone_number, email, website, cuisine_type, price_range, capacity, has_parking, has_wifi, accepts_reservations, is_active, created_at, updated_at)
VALUES 
('La Mama', 'Restaurant traditional românesc cu preparate casnice autentice', 'Strada Mihai Eminescu nr. 45', 'București', '010203', 'România', '+40 21 234 5678', 'contact@lamama.ro', 'https://lamama.ro', 'Românească', '$$', 80, true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Trattoria Italiana', 'Bucătărie italiană autentică cu paste și pizza la cuptor cu lemne', 'Bulevardul Unirii nr. 12', 'București', '030123', 'România', '+40 21 345 6789', 'info@trattoria.ro', 'https://trattoria.ro', 'Italiană', '$$$', 60, true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Sushi Master', 'Cel mai fresh sushi din oraș, ingrediente importate zilnic', 'Strada Victoriei nr. 89', 'București', '010056', 'România', '+40 21 456 7890', 'rezervari@sushimaster.ro', 'https://sushimaster.ro', 'Japoneză', '$$$', 50, false, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Bistro Parisien', 'Ambianță franțuzească elegantă cu specialități pariziene', 'Calea Dorobanți nr. 156', 'București', '010567', 'România', '+40 21 567 8901', 'contact@bistroparisien.ro', 'https://bistroparisien.ro', 'Franțuzească', '$$$$', 45, true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Green Garden', 'Restaurant vegetarian și vegan cu ingrediente organice', 'Strada Plantelor nr. 23', 'Cluj-Napoca', '400123', 'România', '+40 264 123 456', 'hello@greengarden.ro', 'https://greengarden.ro', 'Vegetariană', '$$', 40, true, true, true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserare utilizatori de test
INSERT INTO "user" (first_name, last_name, email, password_hash, phone_number, role, is_active, email_verified, created_at, updated_at)
VALUES 
('Ion', 'Popescu', 'ion.popescu@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+40 722 123 456', 'CLIENT', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Maria', 'Ionescu', 'maria.ionescu@email.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+40 733 234 567', 'CLIENT', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Admin', 'Restaurant', 'admin@lamama.ro', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '+40 722 345 678', 'ADMIN_RESTAURANT', true, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update restaurant_id pentru admin
UPDATE "user" SET restaurant_id = 1 WHERE email = 'admin@lamama.ro';

-- Inserare mese pentru fiecare restaurant
-- La Mama (restaurant_id = 1)
INSERT INTO "table" (restaurant_id, table_number, capacity, location, is_available, position_x, position_y, shape, created_at, updated_at)
VALUES 
(1, 'T1', 2, 'Fereastră', true, 10.00, 10.00, 'round', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'T2', 4, 'Centru', true, 20.00, 15.00, 'square', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'T3', 6, 'Colț', true, 30.00, 10.00, 'rectangle', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'T4', 4, 'Fereastră', true, 10.00, 25.00, 'square', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'T5', 8, 'Terasă', true, 40.00, 20.00, 'rectangle', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Trattoria Italiana (restaurant_id = 2)
INSERT INTO "table" (restaurant_id, table_number, capacity, location, is_available, position_x, position_y, shape, created_at, updated_at)
VALUES 
(2, 'M1', 2, 'Bar', true, 5.00, 5.00, 'round', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'M2', 4, 'Interior', true, 15.00, 10.00, 'square', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'M3', 6, 'VIP', true, 25.00, 15.00, 'rectangle', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'M4', 2, 'Fereastră', true, 10.00, 20.00, 'round', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sushi Master (restaurant_id = 3)
INSERT INTO "table" (restaurant_id, table_number, capacity, location, is_available, position_x, position_y, shape, created_at, updated_at)
VALUES 
(3, 'S1', 2, 'Bar Sushi', true, 8.00, 8.00, 'round', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'S2', 4, 'Zona Privată', true, 18.00, 12.00, 'square', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'S3', 6, 'Tatami Room', true, 28.00, 16.00, 'rectangle', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserare meniuri
INSERT INTO menu (restaurant_id, name, description, menu_type, is_active, created_at, updated_at)
VALUES 
(1, 'Meniu Principal', 'Meniu cu preparate românești tradiționale', 'REGULAR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Meniu Italia', 'Specialități italiene autentice', 'REGULAR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Meniu Sushi', 'Sushi și specialități japoneze', 'REGULAR', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserare iteme de meniu
-- La Mama
INSERT INTO menu_item (menu_id, name, description, price, category, is_vegetarian, is_vegan, is_gluten_free, is_available, display_order, created_at, updated_at)
VALUES 
(1, 'Sarmale', 'Sarmale cu mămăligă și smântână', 35.00, 'Fel Principal', false, false, false, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Mici cu Muștar', '1 porție de mici (5 bucăți) cu muștar și pâine', 25.00, 'Fel Principal', false, false, false, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 'Ciorbă de Burtă', 'Ciorbă de burtă tradițională cu smântână și ardei', 20.00, 'Supă', false, false, false, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Trattoria Italiana
INSERT INTO menu_item (menu_id, name, description, price, category, is_vegetarian, is_vegan, is_gluten_free, is_available, display_order, created_at, updated_at)
VALUES 
(2, 'Pizza Margherita', 'Pizza cu sos de roșii, mozzarella și busuioc', 32.00, 'Pizza', true, false, false, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Pasta Carbonara', 'Paste cu bacon, ouă și parmezan', 38.00, 'Paste', false, false, false, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Lasagna', 'Lasagna cu carne și sos bechamel', 42.00, 'Paste', false, false, false, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sushi Master
INSERT INTO menu_item (menu_id, name, description, price, category, is_vegetarian, is_vegan, is_gluten_free, is_available, display_order, created_at, updated_at)
VALUES 
(3, 'Salmon Roll', '8 bucăți cu somon proaspăt și avocado', 45.00, 'Rolls', false, false, true, true, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'California Roll', '8 bucăți cu crab, avocado și castravete', 38.00, 'Rolls', false, false, true, true, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Vegetarian Roll', '8 bucăți cu legume proaspete', 32.00, 'Rolls', true, true, true, true, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserare câteva rezervări de test
INSERT INTO reservation (user_id, restaurant_id, table_id, reservation_date, reservation_time, duration_minutes, number_of_guests, status, confirmation_code, created_at, updated_at)
VALUES 
(1, 1, 2, CURRENT_DATE + INTERVAL '2 days', '19:00:00', 120, 4, 'CONFIRMED', 'ABC12345', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 5, CURRENT_DATE + INTERVAL '3 days', '20:00:00', 120, 2, 'PENDING', 'XYZ67890', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Inserare feedback
INSERT INTO feedback (user_id, restaurant_id, reservation_id, rating, comment, food_rating, service_rating, ambiance_rating, value_rating, is_visible, created_at, updated_at)
VALUES 
(1, 1, NULL, 5, 'Mâncare delicioasă și servire excelentă! Recomand cu căldură!', 5, 5, 5, 5, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, NULL, 4, 'Pizza foarte bună, atmosferă plăcută. Aș mai veni!', 5, 4, 4, 4, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
