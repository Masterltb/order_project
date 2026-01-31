-- V6__create_menu_item_table.sql
-- Create menu_item table for menu catalog

CREATE TABLE menu_item (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    base_price DECIMAL(12,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    image_url VARCHAR(500),
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    available_from TIME,
    available_to TIME,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_menu_item_category ON menu_item(category);
CREATE INDEX idx_menu_item_status ON menu_item(status);

-- Comments
COMMENT ON TABLE menu_item IS 'Menu items for the restaurant';
COMMENT ON COLUMN menu_item.status IS 'AVAILABLE or UNAVAILABLE';
COMMENT ON COLUMN menu_item.available_from IS 'Optional: available from time (e.g., 10:00)';
COMMENT ON COLUMN menu_item.available_to IS 'Optional: available until time (e.g., 22:00)';
