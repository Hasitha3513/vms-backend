CREATE TABLE IF NOT EXISTS undercarriage_type (
    type_id SERIAL PRIMARY KEY,
    type_code VARCHAR(20) UNIQUE NOT NULL,
    type_name VARCHAR(50) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE
);

INSERT INTO undercarriage_type (type_code, type_name, description)
SELECT 'WHEELED', 'Wheeled', 'Standard wheeled undercarriage'
WHERE NOT EXISTS (SELECT 1 FROM undercarriage_type WHERE type_code = 'WHEELED');

INSERT INTO undercarriage_type (type_code, type_name, description)
SELECT 'TRACKED', 'Tracked', 'Tracked undercarriage'
WHERE NOT EXISTS (SELECT 1 FROM undercarriage_type WHERE type_code = 'TRACKED');

INSERT INTO undercarriage_type (type_code, type_name, description)
SELECT 'SEMI_TRACKED', 'Semi-Tracked', 'Combination wheel and track undercarriage'
WHERE NOT EXISTS (SELECT 1 FROM undercarriage_type WHERE type_code = 'SEMI_TRACKED');

ALTER TABLE IF EXISTS vehicle_type
    ADD COLUMN IF NOT EXISTS undercarriage_type_id INTEGER REFERENCES undercarriage_type (type_id);
