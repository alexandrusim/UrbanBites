-- Allow NULL user_id for guest payments
ALTER TABLE payment ALTER COLUMN user_id DROP NOT NULL;

-- Add comment
COMMENT ON COLUMN payment.user_id IS 'User ID - NULL for guest payments';
