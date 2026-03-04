CREATE TABLE IF NOT EXISTS engine_type (
    enginetype_id SERIAL PRIMARY KEY,
    enginetype_name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS engine_manufacture (
    enginemanufacture_id SERIAL PRIMARY KEY,
    enginemanufacture_name VARCHAR(100) NOT NULL,
    description TEXT
);

INSERT INTO engine_type (enginetype_name, description)
SELECT 'Diesel IC Engine', 'Internal combustion diesel engine'
WHERE NOT EXISTS (SELECT 1 FROM engine_type WHERE enginetype_name = 'Diesel IC Engine');
INSERT INTO engine_type (enginetype_name, description)
SELECT 'Petrol IC Engine', 'Internal combustion petrol engine'
WHERE NOT EXISTS (SELECT 1 FROM engine_type WHERE enginetype_name = 'Petrol IC Engine');
INSERT INTO engine_type (enginetype_name, description)
SELECT 'CNG Engine', 'Compressed natural gas engine'
WHERE NOT EXISTS (SELECT 1 FROM engine_type WHERE enginetype_name = 'CNG Engine');
INSERT INTO engine_type (enginetype_name, description)
SELECT 'LPG Engine', 'Liquefied petroleum gas engine'
WHERE NOT EXISTS (SELECT 1 FROM engine_type WHERE enginetype_name = 'LPG Engine');
INSERT INTO engine_type (enginetype_name, description)
SELECT 'Electric Motor', 'Battery electric propulsion motor'
WHERE NOT EXISTS (SELECT 1 FROM engine_type WHERE enginetype_name = 'Electric Motor');
INSERT INTO engine_type (enginetype_name, description)
SELECT 'Hybrid Powertrain', 'Hybrid engine and electric powertrain'
WHERE NOT EXISTS (SELECT 1 FROM engine_type WHERE enginetype_name = 'Hybrid Powertrain');

INSERT INTO engine_manufacture (enginemanufacture_name, description)
SELECT 'Cummins', 'Cummins engines'
WHERE NOT EXISTS (SELECT 1 FROM engine_manufacture WHERE enginemanufacture_name = 'Cummins');
INSERT INTO engine_manufacture (enginemanufacture_name, description)
SELECT 'Caterpillar', 'Caterpillar engines'
WHERE NOT EXISTS (SELECT 1 FROM engine_manufacture WHERE enginemanufacture_name = 'Caterpillar');
INSERT INTO engine_manufacture (enginemanufacture_name, description)
SELECT 'Ashok Leyland', 'Ashok Leyland engines'
WHERE NOT EXISTS (SELECT 1 FROM engine_manufacture WHERE enginemanufacture_name = 'Ashok Leyland');
INSERT INTO engine_manufacture (enginemanufacture_name, description)
SELECT 'Tata', 'Tata Motors engines'
WHERE NOT EXISTS (SELECT 1 FROM engine_manufacture WHERE enginemanufacture_name = 'Tata');
INSERT INTO engine_manufacture (enginemanufacture_name, description)
SELECT 'Mahindra', 'Mahindra engines'
WHERE NOT EXISTS (SELECT 1 FROM engine_manufacture WHERE enginemanufacture_name = 'Mahindra');
INSERT INTO engine_manufacture (enginemanufacture_name, description)
SELECT 'Kirloskar', 'Kirloskar engines'
WHERE NOT EXISTS (SELECT 1 FROM engine_manufacture WHERE enginemanufacture_name = 'Kirloskar');
