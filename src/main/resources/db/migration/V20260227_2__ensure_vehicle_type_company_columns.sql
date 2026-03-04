ALTER TABLE IF EXISTS vehicle_type
    ADD COLUMN IF NOT EXISTS company_id UUID,
    ADD COLUMN IF NOT EXISTS company_code VARCHAR(50),
    ADD COLUMN IF NOT EXISTS undercarriage_type_id INTEGER;

ALTER TABLE IF EXISTS vehicle_type
    ALTER COLUMN type_code TYPE VARCHAR(50),
    ALTER COLUMN usage_type TYPE VARCHAR(50);

ALTER TABLE IF EXISTS vehicle_type
    DROP CONSTRAINT IF EXISTS vehicle_type_usage_type_check;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_vehicle_type_undercarriage_type'
    ) THEN
        ALTER TABLE vehicle_type
            ADD CONSTRAINT fk_vehicle_type_undercarriage_type
            FOREIGN KEY (undercarriage_type_id) REFERENCES undercarriage_type (type_id);
    END IF;
END $$;

UPDATE vehicle_type vt
SET company_id = vc.company_id
FROM vehicle_category vc
WHERE vt.category_id = vc.category_id
  AND vt.company_id IS NULL;

UPDATE vehicle_type vt
SET company_id = c.company_id
FROM (
    SELECT company_id
    FROM company
    ORDER BY created_at NULLS LAST, company_name, company_code
    LIMIT 1
) c
WHERE vt.company_id IS NULL;

UPDATE vehicle_type vt
SET company_code = c.company_code
FROM company c
WHERE vt.company_id = c.company_id
  AND (vt.company_code IS NULL OR vt.company_code <> c.company_code);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM vehicle_type
        WHERE company_id IS NULL OR company_code IS NULL
    ) THEN
        RAISE EXCEPTION 'vehicle_type has rows with NULL company_id/company_code; fix data before applying NOT NULL';
    END IF;
END $$;

ALTER TABLE IF EXISTS vehicle_type
    ALTER COLUMN company_id SET NOT NULL,
    ALTER COLUMN company_code SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_vehicle_type_company_id'
    ) THEN
        ALTER TABLE vehicle_type
            ADD CONSTRAINT fk_vehicle_type_company_id
            FOREIGN KEY (company_id) REFERENCES company (company_id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_vehicle_type_company_code'
    ) THEN
        ALTER TABLE vehicle_type
            ADD CONSTRAINT fk_vehicle_type_company_code
            FOREIGN KEY (company_code) REFERENCES company (company_code) ON UPDATE CASCADE;
    END IF;
END $$;

ALTER TABLE IF EXISTS vehicle_type
    DROP CONSTRAINT IF EXISTS vehicle_type_type_name_type_code_key;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_vehicle_type_company_name_code'
    ) THEN
        ALTER TABLE vehicle_type
            ADD CONSTRAINT uk_vehicle_type_company_name_code
            UNIQUE (company_id, type_name, type_code);
    END IF;
END $$;