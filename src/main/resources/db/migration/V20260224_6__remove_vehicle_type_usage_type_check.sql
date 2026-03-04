ALTER TABLE IF EXISTS vehicle_type
    ALTER COLUMN usage_type TYPE VARCHAR(50);

ALTER TABLE IF EXISTS vehicle_type
    DROP CONSTRAINT IF EXISTS vehicle_type_usage_type_check;
