-- Normalize supplier table across environments:
-- - ensure tenant columns exist
-- - ensure supplier_code exists and can be generated
-- - backfill missing values where possible

CREATE SEQUENCE IF NOT EXISTS supplier_code_seq START 1;

ALTER TABLE supplier
    ADD COLUMN IF NOT EXISTS company_id UUID REFERENCES company (company_id) ON DELETE CASCADE,
    ADD COLUMN IF NOT EXISTS company_code VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
    ADD COLUMN IF NOT EXISTS contact_name VARCHAR(100),
    ADD COLUMN IF NOT EXISTS supplier_code VARCHAR(50);

-- Backfill company context using first active company when missing.
UPDATE supplier s
SET company_id = c.company_id
FROM (
    SELECT company_id
    FROM company
    WHERE is_active = TRUE
    ORDER BY created_at ASC
    LIMIT 1
) c
WHERE s.company_id IS NULL;

UPDATE supplier s
SET company_code = c.company_code
FROM company c
WHERE s.company_id = c.company_id
  AND s.company_code IS NULL;

-- Backfill supplier_code when missing.
UPDATE supplier
SET supplier_code = 'SUP' || LPAD(nextval('supplier_code_seq')::text, 6, '0')
WHERE supplier_code IS NULL;

ALTER TABLE supplier
    ALTER COLUMN supplier_code SET DEFAULT ('SUP' || LPAD(nextval('supplier_code_seq')::text, 6, '0'));

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'supplier'
          AND column_name = 'supplier_code'
    ) THEN
        ALTER TABLE supplier ALTER COLUMN supplier_code SET NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'supplier'
          AND column_name = 'company_id'
    ) AND NOT EXISTS (SELECT 1 FROM supplier WHERE company_id IS NULL) THEN
        ALTER TABLE supplier ALTER COLUMN company_id SET NOT NULL;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'supplier'
          AND column_name = 'company_code'
    ) AND NOT EXISTS (SELECT 1 FROM supplier WHERE company_code IS NULL) THEN
        ALTER TABLE supplier ALTER COLUMN company_code SET NOT NULL;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_supplier_supplier_code ON supplier (supplier_code);

