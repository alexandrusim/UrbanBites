CREATE TABLE activity_logs (
    log_id SERIAL PRIMARY KEY,
    user_id INT REFERENCES "user"(user_id) ON DELETE SET NULL,
    action VARCHAR(255) NOT NULL,
    endpoint VARCHAR(500),
    http_method VARCHAR(10),
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status_code INT,
    response_time_ms BIGINT
);

CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);

CREATE INDEX idx_activity_logs_timestamp ON activity_logs(timestamp DESC);

CREATE INDEX idx_activity_logs_endpoint ON activity_logs(endpoint);
