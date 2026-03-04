CREATE TABLE IF NOT EXISTS vehicle_puc (
    puc_id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id              UUID,
    company_id              UUID,
    companyvehicle_id       UUID,
    certificate_number      VARCHAR(100),
    issuing_center          VARCHAR(150),
    issue_date              DATE,
    expiry_date             DATE,
    co_emission_percent     DECIMAL(6, 3),
    hc_emission_ppm         DECIMAL(8, 2),
    test_result             VARCHAR(20) DEFAULT 'Pass',
    puc_status              VARCHAR(20) DEFAULT 'Valid',
    renewal_reminder_days   INTEGER DEFAULT 15,
    is_current              BOOLEAN DEFAULT TRUE,
    created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS vehicle_id UUID;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS companyvehicle_id UUID;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS certificate_number VARCHAR(100);
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS issuing_center VARCHAR(150);
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS issue_date DATE;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS expiry_date DATE;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS co_emission_percent DECIMAL(6, 3);
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS hc_emission_ppm DECIMAL(8, 2);
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS test_result VARCHAR(20) DEFAULT 'Pass';
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS puc_status VARCHAR(20) DEFAULT 'Valid';
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS renewal_reminder_days INTEGER DEFAULT 15;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS is_current BOOLEAN DEFAULT TRUE;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE vehicle_puc ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vpuc_vehicle'
    ) THEN
        ALTER TABLE vehicle_puc
            ADD CONSTRAINT fk_vpuc_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vpuc_company'
    ) THEN
        ALTER TABLE vehicle_puc
            ADD CONSTRAINT fk_vpuc_company FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vpuc_company_vehicle'
    ) THEN
        ALTER TABLE vehicle_puc
            ADD CONSTRAINT fk_vpuc_company_vehicle FOREIGN KEY (companyvehicle_id) REFERENCES company_vehicles(companyvehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_vpuc_test_result'
    ) THEN
        ALTER TABLE vehicle_puc
            ADD CONSTRAINT chk_vpuc_test_result CHECK (test_result IN ('Pass', 'Fail'));
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_vpuc_status'
    ) THEN
        ALTER TABLE vehicle_puc
            ADD CONSTRAINT chk_vpuc_status CHECK (puc_status IN ('Valid', 'Expired'));
    END IF;
END$$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_vpuc_certificate_number ON vehicle_puc(certificate_number);
CREATE INDEX IF NOT EXISTS idx_vpuc_vehicle ON vehicle_puc(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vpuc_expiry ON vehicle_puc(expiry_date);

DROP TRIGGER IF EXISTS trg_vehicle_puc_upd ON vehicle_puc;
CREATE TRIGGER trg_vehicle_puc_upd
    BEFORE UPDATE ON vehicle_puc
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
