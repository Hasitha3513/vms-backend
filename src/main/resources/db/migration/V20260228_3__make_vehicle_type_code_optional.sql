ALTER TABLE IF EXISTS vehicle_type
    ALTER COLUMN type_code DROP NOT NULL;

ALTER TABLE IF EXISTS vehicle_type
    DROP CONSTRAINT IF EXISTS vehicle_type_type_name_type_code_key;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM vehicle_type
        GROUP BY type_name
        HAVING COUNT(*) > 1
    ) THEN
        ALTER TABLE vehicle_type
            ADD CONSTRAINT vehicle_type_type_name_key
            UNIQUE (type_name);
    END IF;
END $$;
