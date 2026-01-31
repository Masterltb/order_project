-- V11__create_order_item_table.sql
-- Create order_item table

CREATE TABLE order_item (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id),
    menu_item_id BIGINT NOT NULL REFERENCES menu_item(id),
    menu_item_name VARCHAR(200) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    unit_price DECIMAL(12,2) NOT NULL,
    options_detail JSONB,
    options_price DECIMAL(12,2) NOT NULL DEFAULT 0,
    line_total DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    note TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_order_item_order ON order_item(order_id);
CREATE INDEX idx_order_item_status ON order_item(status);

-- Comments
COMMENT ON TABLE order_item IS 'Order line items with snapshot pricing and options';
COMMENT ON COLUMN order_item.menu_item_name IS 'Snapshot of menu item name at order time';
COMMENT ON COLUMN order_item.options_detail IS 'JSONB snapshot of selected options';
COMMENT ON COLUMN order_item.line_total IS '(unit_price + options_price) * quantity';
