ALTER TABLE IF EXISTS vehicle_category
    ADD COLUMN IF NOT EXISTS company_id UUID,
    ADD COLUMN IF NOT EXISTS company_code VARCHAR(50);

ALTER TABLE IF EXISTS vehicle_category
    ALTER COLUMN category_code TYPE VARCHAR(50);

UPDATE vehicle_category vc
SET company_id = c.company_id
FROM (
    SELECT company_id
    FROM company
    ORDER BY created_at NULLS LAST, company_name, company_code
    LIMIT 1
) c
WHERE vc.company_id IS NULL;

UPDATE vehicle_category vc
SET company_code = c.company_code
FROM company c
WHERE vc.company_id = c.company_id
  AND (vc.company_code IS NULL OR vc.company_code <> c.company_code);

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM vehicle_category
        WHERE company_id IS NULL OR company_code IS NULL
    ) THEN
        RAISE EXCEPTION 'vehicle_category has rows with NULL company_id/company_code; fix data before applying NOT NULL';
    END IF;
END $$;

ALTER TABLE IF EXISTS vehicle_category
    ALTER COLUMN company_id SET NOT NULL,
    ALTER COLUMN company_code SET NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_vehicle_category_company_id'
    ) THEN
        ALTER TABLE vehicle_category
            ADD CONSTRAINT fk_vehicle_category_company_id
            FOREIGN KEY (company_id) REFERENCES company (company_id) ON DELETE CASCADE;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_vehicle_category_company_code'
    ) THEN
        ALTER TABLE vehicle_category
            ADD CONSTRAINT fk_vehicle_category_company_code
            FOREIGN KEY (company_code) REFERENCES company (company_code) ON UPDATE CASCADE;
    END IF;
END $$;

ALTER TABLE IF EXISTS vehicle_category
    DROP CONSTRAINT IF EXISTS vehicle_category_category_name_category_code_key;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uk_vehicle_category_company_name_code'
    ) THEN
        ALTER TABLE vehicle_category
            ADD CONSTRAINT uk_vehicle_category_company_name_code
            UNIQUE (company_id, category_name, category_code);
    END IF;
END $$;