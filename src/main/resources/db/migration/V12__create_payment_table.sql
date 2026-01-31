-- V12__create_payment_table.sql
-- Create payment table for order payments

CREATE TABLE payment (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL REFERENCES "order"(id),
    payment_method VARCHAR(20) NOT NULL,
    amount DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    transaction_ref VARCHAR(100),
    paid_at TIMESTAMPTZ,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes
CREATE INDEX idx_payment_order ON payment(order_id);
CREATE INDEX idx_payment_status ON payment(status);

-- Comments
COMMENT ON TABLE payment IS 'Payment transactions for orders';
COMMENT ON COLUMN payment.payment_method IS 'CASH, MOMO, or ZALOPAY';
COMMENT ON COLUMN payment.status IS 'PENDING, SUCCEEDED, or FAILED';
COMMENT ON COLUMN payment.transaction_ref IS 'External transaction reference for e-wallet payments';
