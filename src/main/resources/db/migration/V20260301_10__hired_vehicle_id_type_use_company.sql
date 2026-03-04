ALTER TABLE hiredvehicleidtype
    ADD COLUMN IF NOT EXISTS idtype_com UUID,
    ADD COLUMN IF NOT EXISTS company_code VARCHAR;

UPDATE hiredvehicleidtype hvit
SET idtype_com = s.company_id
FROM supplier s
WHERE hvit.idtype_sup = s.supplier_id
  AND hvit.idtype_com IS NULL;

UPDATE hiredvehicleidtype hvit
SET idtype_com = fallback.company_id
FROM (
    SELECT c.company_id
    FROM company c
    WHERE COALESCE(c.is_active, TRUE) = TRUE
    ORDER BY c.created_at ASC
    LIMIT 1
) AS fallback
WHERE hvit.idtype_com IS NULL;

UPDATE hiredvehicleidtype hvit
SET company_code = c.company_code
FROM company c
WHERE hvit.idtype_com = c.company_id
  AND (hvit.company_code IS NULL OR trim(hvit.company_code) = '');

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_hiredvehicleidtype_company'
    ) THEN
        ALTER TABLE hiredvehicleidtype
            ADD CONSTRAINT fk_hiredvehicleidtype_company
            FOREIGN KEY (idtype_com) REFERENCES company(company_id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM hiredvehicleidtype WHERE idtype_com IS NULL OR company_code IS NULL OR trim(company_code) = ''
    ) THEN
        ALTER TABLE hiredvehicleidtype
            ALTER COLUMN idtype_com SET NOT NULL,
            ALTER COLUMN company_code SET NOT NULL;
    END IF;
END $$;

ALTER TABLE hiredvehicleidtype DROP CONSTRAINT IF EXISTS uq_hiredvehicleidtype_supplier_code;
ALTER TABLE hiredvehicleidtype DROP CONSTRAINT IF EXISTS hiredvehicleidtype_idtype_sup_fkey;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'uq_hiredvehicleidtype_company_code'
    ) THEN
        ALTER TABLE hiredvehicleidtype
            ADD CONSTRAINT uq_hiredvehicleidtype_company_code UNIQUE (idtype_com, idtype_code);
    END IF;
END $$;

DROP INDEX IF EXISTS idx_hiredvehicleidtype_supplier;
CREATE INDEX IF NOT EXISTS idx_hiredvehicleidtype_company ON hiredvehicleidtype(idtype_com);
CREATE INDEX IF NOT EXISTS idx_hiredvehicleidtype_type ON hiredvehicleidtype(idtype_typeid);

ALTER TABLE hiredvehicleidtype
    DROP COLUMN IF EXISTS idtype_sup,
    DROP COLUMN IF EXISTS supplier_code;
