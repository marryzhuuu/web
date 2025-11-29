CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS point_results (
    id BIGSERIAL PRIMARY KEY,
    x DOUBLE PRECISION NOT NULL,
    y DOUBLE PRECISION NOT NULL,
    r DOUBLE PRECISION NOT NULL,
    result BOOLEAN NOT NULL,
    check_time TIMESTAMP NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_point_results_user_id ON point_results(user_id);
CREATE INDEX idx_point_results_check_time ON point_results(check_time DESC);