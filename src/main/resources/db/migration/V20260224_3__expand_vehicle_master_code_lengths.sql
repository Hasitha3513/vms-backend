ALTER TABLE IF EXISTS vehicle_category
    ALTER COLUMN category_code TYPE VARCHAR(50);

ALTER TABLE IF EXISTS vehicle_type
    ALTER COLUMN type_code TYPE VARCHAR(50);

ALTER TABLE IF EXISTS vehicle_manufacturer
    ALTER COLUMN manufacturer_code TYPE VARCHAR(50);
