ALTER TABLE "user" ADD COLUMN two_fa_enabled BOOLEAN DEFAULT FALSE;
ALTER TABLE "user" ADD COLUMN two_fa_secret VARCHAR(255);
ALTER TABLE "user" ADD COLUMN two_fa_verified BOOLEAN DEFAULT FALSE;

ALTER TABLE "user" ADD COLUMN two_fa_created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE "user" ADD COLUMN two_fa_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE "user" ADD COLUMN two_fa_last_verified TIMESTAMP;
ALTER TABLE "user" ADD COLUMN two_fa_method VARCHAR(50) DEFAULT 'TOTP';

ALTER TABLE "user" ADD COLUMN two_fa_failed_attempts INT DEFAULT 0;
ALTER TABLE "user" ADD COLUMN two_fa_locked_until TIMESTAMP;

ALTER TABLE "user" ADD COLUMN two_fa_backup_codes TEXT;

CREATE INDEX idx_user_two_fa_enabled ON "user"(two_fa_enabled);
CREATE INDEX idx_user_two_fa_last_verified ON "user"(two_fa_last_verified);

CREATE TABLE two_fa_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(user_id) ON DELETE CASCADE,
    action VARCHAR(50) NOT NULL,
    success BOOLEAN DEFAULT FALSE,
    error_message VARCHAR(255),
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_two_fa_log_user_id ON two_fa_log(user_id);
CREATE INDEX idx_two_fa_log_created_at ON two_fa_log(created_at);
CREATE INDEX idx_two_fa_log_user_created ON two_fa_log(user_id, created_at);
