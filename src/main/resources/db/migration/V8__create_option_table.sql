-- V8__create_option_table.sql
-- Create option table for individual options

CREATE TABLE option (
    id BIGSERIAL PRIMARY KEY,
    option_group_id BIGINT NOT NULL REFERENCES option_group(id),
    name VARCHAR(100) NOT NULL,
    price_delta DECIMAL(12,2) NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_option_group ON option(option_group_id);

-- Comments
COMMENT ON TABLE option IS 'Individual options (Size L, Extra cheese, etc.)';
COMMENT ON COLUMN option.price_delta IS 'Price adjustment when this option is selected';
