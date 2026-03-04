CREATE TABLE IF NOT EXISTS usage_type (
    type_id SERIAL PRIMARY KEY,
    type_code VARCHAR(20) UNIQUE NOT NULL,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO usage_type (type_code, type_name, description)
SELECT 'PERSONAL', 'Personal', 'Personal use'
WHERE NOT EXISTS (SELECT 1 FROM usage_type WHERE type_code = 'PERSONAL');

INSERT INTO usage_type (type_code, type_name, description)
SELECT 'COMMERCIAL', 'Commercial', 'Commercial use'
WHERE NOT EXISTS (SELECT 1 FROM usage_type WHERE type_code = 'COMMERCIAL');

INSERT INTO usage_type (type_code, type_name, description)
SELECT 'BOTH', 'Both', 'Personal and commercial use'
WHERE NOT EXISTS (SELECT 1 FROM usage_type WHERE type_code = 'BOTH');
