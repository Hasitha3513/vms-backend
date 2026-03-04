ALTER TABLE IF EXISTS vehicle_type
    DROP CONSTRAINT IF EXISTS uk_vehicle_type_company_name_code,
    DROP CONSTRAINT IF EXISTS fk_vehicle_type_company_id,
    DROP CONSTRAINT IF EXISTS fk_vehicle_type_company_code;

ALTER TABLE IF EXISTS vehicle_type
    DROP CONSTRAINT IF EXISTS vehicle_type_type_name_type_code_key;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM vehicle_type
        GROUP BY type_name, type_code
        HAVING COUNT(*) > 1
    ) THEN
        ALTER TABLE vehicle_type
            ADD CONSTRAINT vehicle_type_type_name_type_code_key
            UNIQUE (type_name, type_code);
    END IF;
END $$;

ALTER TABLE IF EXISTS vehicle_type
    DROP COLUMN IF EXISTS company_id,
    DROP COLUMN IF EXISTS company_code;