-- V10__create_order_table.sql
-- Create order table with optimistic locking

CREATE TABLE "order" (
    id BIGSERIAL PRIMARY KEY,
    table_id BIGINT NOT NULL REFERENCES dining_table(id),
    order_number VARCHAR(20) NOT NULL UNIQUE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_CONFIRM',
    subtotal DECIMAL(12,2) NOT NULL DEFAULT 0,
    discount_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    total_amount DECIMAL(12,2) NOT NULL DEFAULT 0,
    note TEXT,
    is_closed BOOLEAN NOT NULL DEFAULT false,
    version INTEGER NOT NULL DEFAULT 0,
    created_by BIGINT REFERENCES users(id),
    updated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_order_table ON "order"(table_id);
CREATE INDEX idx_order_status ON "order"(status);
CREATE INDEX idx_order_is_closed ON "order"(is_closed);
CREATE INDEX idx_order_created_at ON "order"(created_at);

-- Comments
COMMENT ON TABLE "order" IS 'Orders with optimistic locking (version field)';
COMMENT ON COLUMN "order".version IS 'Optimistic locking version counter';
COMMENT ON COLUMN "order".is_closed IS 'True if order is paid or cancelled';
