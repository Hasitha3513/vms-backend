ALTER TABLE IF EXISTS vehicle_manufacturer
    DROP CONSTRAINT IF EXISTS vehicle_manufacturer_manufacturer_code_key;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_vehicle_manufacturer_name_country'
    ) THEN
        ALTER TABLE vehicle_manufacturer
            ADD CONSTRAINT uq_vehicle_manufacturer_name_country UNIQUE (manufacturer_name, country);
    END IF;
END $$;
