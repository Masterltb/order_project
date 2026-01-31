-- V7__create_option_group_table.sql
-- Create option_group table for menu options

CREATE TABLE option_group (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    selection_type VARCHAR(20) NOT NULL DEFAULT 'SINGLE',
    min_select INTEGER NOT NULL DEFAULT 0,
    max_select INTEGER NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Comments
COMMENT ON TABLE option_group IS 'Option groups (Size, Topping, Sugar level, etc.)';
COMMENT ON COLUMN option_group.selection_type IS 'SINGLE or MULTIPLE selection';
