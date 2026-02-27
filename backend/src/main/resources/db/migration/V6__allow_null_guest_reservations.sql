-- ========================================
-- Migration: Allow NULL for guest reservations
-- ========================================
-- This migration modifies the reservation table to allow guest reservations
-- where user_id can be NULL (for unauthenticated users)
-- and table_id can be NULL (when table hasn't been assigned yet)

-- Drop the NOT NULL constraint on user_id to allow guest reservations
ALTER TABLE reservation ALTER COLUMN user_id DROP NOT NULL;

-- Drop the NOT NULL constraint on table_id to allow unassigned reservations
ALTER TABLE reservation ALTER COLUMN table_id DROP NOT NULL;

-- Add a check constraint to ensure either user_id is provided OR guest info is in special_requests
-- This ensures we always have some way to identify who made the reservation
ALTER TABLE reservation ADD CONSTRAINT check_user_or_guest
    CHECK (user_id IS NOT NULL OR special_requests IS NOT NULL);

-- Update index to handle NULL user_id values
DROP INDEX IF EXISTS idx_reservation_user_status;
CREATE INDEX idx_reservation_user_status ON reservation(user_id, status) WHERE user_id IS NOT NULL;

-- Add index for guest reservations (where user_id is NULL)
CREATE INDEX idx_reservation_guest_status ON reservation(status) WHERE user_id IS NULL;

-- Add index for confirmation code lookups (important for guest reservation status checks)
-- This index already exists from V1, but we ensure it's optimized for guest lookups
-- DROP INDEX IF EXISTS idx_reservation_confirmation;
-- CREATE UNIQUE INDEX idx_reservation_confirmation ON reservation(confirmation_code);
