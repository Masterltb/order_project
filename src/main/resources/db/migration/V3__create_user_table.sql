-- V3__create_user_table.sql
-- Create users table for authentication

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Index for login lookup
CREATE INDEX idx_users_username ON users(username);

-- Comments
COMMENT ON TABLE users IS 'Users for authentication (ADMIN, CASHIER, STAFF)';
COMMENT ON COLUMN users.role IS 'ADMIN, CASHIER, or STAFF';
