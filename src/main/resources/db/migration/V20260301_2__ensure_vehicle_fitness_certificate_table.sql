CREATE TABLE IF NOT EXISTS vehicle_fitness_certificate (
    fitness_id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id              UUID,
    company_id              UUID,
    companyvehicle_id       UUID,
    certificate_number      VARCHAR(100),
    issuing_authority       VARCHAR(150),
    inspection_center       VARCHAR(150),
    inspector_id            VARCHAR(100),
    inspector_name          VARCHAR(150),
    issue_date              DATE,
    expiry_date             DATE,
    validity_duration_years INTEGER,
    inspection_result_id    INTEGER,
    remarks                 TEXT,
    renewal_reminder_days   INTEGER DEFAULT 30,
    fitness_status          VARCHAR(20) DEFAULT 'Valid',
    is_current              BOOLEAN DEFAULT TRUE,
    created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS vehicle_id UUID;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS companyvehicle_id UUID;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS certificate_number VARCHAR(100);
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS issuing_authority VARCHAR(150);
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS inspection_center VARCHAR(150);
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS inspector_id VARCHAR(100);
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS inspector_name VARCHAR(150);
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS issue_date DATE;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS expiry_date DATE;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS validity_duration_years INTEGER;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS inspection_result_id INTEGER;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS remarks TEXT;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS renewal_reminder_days INTEGER DEFAULT 30;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS fitness_status VARCHAR(20) DEFAULT 'Valid';
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS is_current BOOLEAN DEFAULT TRUE;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE vehicle_fitness_certificate ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vfit_vehicle'
    ) THEN
        ALTER TABLE vehicle_fitness_certificate
            ADD CONSTRAINT fk_vfit_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vfit_company'
    ) THEN
        ALTER TABLE vehicle_fitness_certificate
            ADD CONSTRAINT fk_vfit_company FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vfit_company_vehicle'
    ) THEN
        ALTER TABLE vehicle_fitness_certificate
            ADD CONSTRAINT fk_vfit_company_vehicle FOREIGN KEY (companyvehicle_id) REFERENCES company_vehicles(companyvehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vfit_inspection_result'
    ) THEN
        ALTER TABLE vehicle_fitness_certificate
            ADD CONSTRAINT fk_vfit_inspection_result FOREIGN KEY (inspection_result_id) REFERENCES inspection_result(result_id);
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_vfit_status'
    ) THEN
        ALTER TABLE vehicle_fitness_certificate
            ADD CONSTRAINT chk_vfit_status CHECK (fitness_status IN ('Valid', 'Expired', 'Suspended'));
    END IF;
END$$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_vfit_certificate_number ON vehicle_fitness_certificate(certificate_number);
CREATE INDEX IF NOT EXISTS idx_vfit_vehicle ON vehicle_fitness_certificate(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vfit_expiry ON vehicle_fitness_certificate(expiry_date);

DROP TRIGGER IF EXISTS trg_vehicle_fitness_certificate_upd ON vehicle_fitness_certificate;
CREATE TRIGGER trg_vehicle_fitness_certificate_upd
    BEFORE UPDATE ON vehicle_fitness_certificate
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
