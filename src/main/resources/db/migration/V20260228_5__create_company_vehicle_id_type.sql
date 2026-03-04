CREATE TABLE IF NOT EXISTS companyvehicleidtype (
    idtype_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    idtype_com UUID REFERENCES company (company_id) ON DELETE CASCADE,
    company_code VARCHAR(50) REFERENCES company (company_code) ON UPDATE CASCADE,
    idtype_typeid UUID REFERENCES vehicle_type (type_id) ON DELETE CASCADE,
    idtype_code VARCHAR(10),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_companyvehicleidtype_company ON companyvehicleidtype (idtype_com);
CREATE INDEX IF NOT EXISTS idx_companyvehicleidtype_type ON companyvehicleidtype (idtype_typeid);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname = current_schema()
          AND indexname = 'uq_companyvehicleidtype_company_code_ci'
    ) THEN
        IF NOT EXISTS (
            SELECT 1
            FROM companyvehicleidtype
            WHERE idtype_com IS NOT NULL AND idtype_code IS NOT NULL
            GROUP BY idtype_com, lower(trim(idtype_code))
            HAVING COUNT(*) > 1
        ) THEN
            EXECUTE 'CREATE UNIQUE INDEX uq_companyvehicleidtype_company_code_ci ON companyvehicleidtype (idtype_com, lower(trim(idtype_code))) WHERE idtype_code IS NOT NULL';
        END IF;
    END IF;
END $$;

DROP TRIGGER IF EXISTS trg_companyvehicleidtype_upd ON companyvehicleidtype;
CREATE TRIGGER trg_companyvehicleidtype_upd BEFORE UPDATE ON companyvehicleidtype FOR EACH ROW EXECUTE FUNCTION set_updated_at();
