-- V2__create_dining_table_table.sql
-- Create dining_table for restaurant tables

CREATE TABLE dining_table (
    id BIGSERIAL PRIMARY KEY,
    area_id BIGINT NOT NULL REFERENCES area(id),
    table_code VARCHAR(10) NOT NULL,
    capacity INTEGER DEFAULT 4,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    CONSTRAINT uk_dining_table_area_code UNIQUE(area_id, table_code)
);

-- Indexes
CREATE INDEX idx_dining_table_area ON dining_table(area_id);
CREATE INDEX idx_dining_table_status ON dining_table(status);

-- Comments
COMMENT ON TABLE dining_table IS 'Restaurant tables (A1, B8, VIP3, etc.)';
COMMENT ON COLUMN dining_table.table_code IS 'Table code within area (1, 2, 3...)';
COMMENT ON COLUMN dining_table.status IS 'AVAILABLE, OCCUPIED, or DISABLED';
