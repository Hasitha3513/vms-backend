ALTER TABLE IF EXISTS vehicle_type
    DROP CONSTRAINT IF EXISTS uk_vehicle_type_company_name_code,
    DROP CONSTRAINT IF EXISTS vehicle_type_type_name_type_code_key;

ALTER TABLE IF EXISTS vehicle_type
    DROP COLUMN IF EXISTS type_code;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'vehicle_type_type_name_key'
    ) THEN
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
    END IF;
END $$;
