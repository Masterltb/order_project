-- V1__create_area_table.sql
-- Create area table for restaurant zones

CREATE TABLE area (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    sort_order INTEGER DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Index for sorting
CREATE INDEX idx_area_sort ON area(sort_order);

-- Comments
COMMENT ON TABLE area IS 'Restaurant areas/zones (A, B, C, VIP)';
COMMENT ON COLUMN area.code IS 'Short code for area (A, B, C, VIP)';
COMMENT ON COLUMN area.status IS 'ACTIVE or INACTIVE';
