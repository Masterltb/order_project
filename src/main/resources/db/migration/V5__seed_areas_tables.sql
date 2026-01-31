-- V5__seed_areas_tables.sql
-- Seed areas and tables for development

-- Areas
INSERT INTO area (code, name, sort_order, status, created_at, updated_at) VALUES
('C', 'Khu C', 1, 'ACTIVE', NOW(), NOW()),
('B', 'Khu B', 2, 'ACTIVE', NOW(), NOW()),
('A', 'Khu A', 3, 'ACTIVE', NOW(), NOW()),
('VIP', 'Khu VIP', 4, 'ACTIVE', NOW(), NOW());

-- Tables for Khu C (5 tables: C1-C5)
INSERT INTO dining_table (area_id, table_code, capacity, status, created_at, updated_at)
SELECT a.id, 'C' || g.n, 4, 'AVAILABLE', NOW(), NOW()
FROM area a, generate_series(1, 5) AS g(n)
WHERE a.code = 'C';

-- Tables for Khu B (8 tables: B1-B8)
INSERT INTO dining_table (area_id, table_code, capacity, status, created_at, updated_at)
SELECT a.id, 'B' || g.n, 4, 'AVAILABLE', NOW(), NOW()
FROM area a, generate_series(1, 8) AS g(n)
WHERE a.code = 'B';

-- Tables for Khu A (4 tables: A1-A4)
INSERT INTO dining_table (area_id, table_code, capacity, status, created_at, updated_at)
SELECT a.id, 'A' || g.n, 4, 'AVAILABLE', NOW(), NOW()
FROM area a, generate_series(1, 4) AS g(n)
WHERE a.code = 'A';

-- Tables for VIP (4 tables: VIP1-VIP4, capacity 6)
INSERT INTO dining_table (area_id, table_code, capacity, status, created_at, updated_at)
SELECT a.id, 'VIP' || g.n, 6, 'AVAILABLE', NOW(), NOW()
FROM area a, generate_series(1, 4) AS g(n)
WHERE a.code = 'VIP';

-- Comments
COMMENT ON TABLE area IS '4 areas: C(5), B(8), A(4), VIP(4) = 21 tables total';
