-- V9__create_menu_item_option_group.sql
-- Junction table for menu_item and option_group

CREATE TABLE menu_item_option_group (
    menu_item_id BIGINT NOT NULL REFERENCES menu_item(id),
    option_group_id BIGINT NOT NULL REFERENCES option_group(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (menu_item_id, option_group_id)
);

-- Indexes
CREATE INDEX idx_miog_menu_item ON menu_item_option_group(menu_item_id);
CREATE INDEX idx_miog_option_group ON menu_item_option_group(option_group_id);

-- Comments
COMMENT ON TABLE menu_item_option_group IS 'Many-to-many mapping between menu items and option groups';
