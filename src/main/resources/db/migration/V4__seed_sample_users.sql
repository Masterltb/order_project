-- V4__seed_sample_users.sql
-- Seed sample users for development

-- Password: admin123 (BCrypt encoded)
INSERT INTO users (username, password_hash, full_name, role, enabled, created_at, updated_at)
VALUES (
    'admin@restaurant.com',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZRGdjGj/n3.GfO16i7LoOJPPJqYmG',
    'System Admin',
    'ADMIN',
    true,
    NOW(),
    NOW()
);

-- Password: cashier123 (BCrypt encoded)
INSERT INTO users (username, password_hash, full_name, role, enabled, created_at, updated_at)
VALUES (
    'cashier@restaurant.com',
    '$2a$10$dTvMnGXIQ/cEOcT3j2c4B.bVvLH3dxEWN/xHdLhAz8WGZbNMxV1Gy',
    'Thu Ngan',
    'CASHIER',
    true,
    NOW(),
    NOW()
);

-- Password: staff123 (BCrypt encoded)
INSERT INTO users (username, password_hash, full_name, role, enabled, created_at, updated_at)
VALUES (
    'staff@restaurant.com',
    '$2a$10$LOMPPLe8Hk5gUNQQBx5N8.wYzAqV7X9Y3h0k6i6FrAplLzNMgSFWi',
    'Nhan Vien',
    'STAFF',
    true,
    NOW(),
    NOW()
);

-- Comments
COMMENT ON TABLE users IS 'Sample users: admin123, cashier123, staff123';
