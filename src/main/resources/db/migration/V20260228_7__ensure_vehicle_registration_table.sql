CREATE TABLE IF NOT EXISTS vehicle_registration (
    registration_id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id              UUID,
    company_id              UUID,
    companyvehicle_id       UUID,
    registration_number     VARCHAR(50),
    registration_date       DATE,
    registration_expiry     DATE,
    registering_authority   VARCHAR(150),
    registration_state      VARCHAR(100),
    registration_city       VARCHAR(100),
    rc_book_number          VARCHAR(100),
    rc_status               VARCHAR(20),
    number_plate_type_id    INTEGER,
    renewal_reminder_days   INTEGER DEFAULT 30,
    notes                   TEXT,
    is_current              BOOLEAN DEFAULT TRUE,
    created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS vehicle_id UUID;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS companyvehicle_id UUID;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS registration_number VARCHAR(50);
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS registration_date DATE;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS registration_expiry DATE;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS registering_authority VARCHAR(150);
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS registration_state VARCHAR(100);
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS registration_city VARCHAR(100);
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS rc_book_number VARCHAR(100);
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS rc_status VARCHAR(20);
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS number_plate_type_id INTEGER;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS renewal_reminder_days INTEGER DEFAULT 30;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS is_current BOOLEAN DEFAULT TRUE;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE vehicle_registration ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'vehicle_registration'
          AND column_name = 'company_vehicle_id'
    ) THEN
        UPDATE vehicle_registration
        SET companyvehicle_id = company_vehicle_id
        WHERE companyvehicle_id IS NULL AND company_vehicle_id IS NOT NULL;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_vreg_vehicle'
    ) THEN
        ALTER TABLE vehicle_registration
            ADD CONSTRAINT fk_vreg_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_vreg_company'
    ) THEN
        ALTER TABLE vehicle_registration
            ADD CONSTRAINT fk_vreg_company FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_vreg_company_vehicle'
    ) THEN
        ALTER TABLE vehicle_registration
            ADD CONSTRAINT fk_vreg_company_vehicle FOREIGN KEY (companyvehicle_id) REFERENCES company_vehicles(companyvehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'fk_vreg_number_plate_type'
    ) THEN
        ALTER TABLE vehicle_registration
            ADD CONSTRAINT fk_vreg_number_plate_type FOREIGN KEY (number_plate_type_id) REFERENCES number_plate_type(plate_type_id);
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'chk_vreg_rc_status'
    ) THEN
        ALTER TABLE vehicle_registration
            ADD CONSTRAINT chk_vreg_rc_status CHECK (rc_status IN ('Valid', 'Expired', 'Suspended'));
    END IF;
END$$;

CREATE INDEX IF NOT EXISTS idx_vreg_vehicle ON vehicle_registration(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vreg_expiry ON vehicle_registration(registration_expiry);

DROP TRIGGER IF EXISTS trg_vehicle_registration_upd ON vehicle_registration;
CREATE TRIGGER trg_vehicle_registration_upd
    BEFORE UPDATE ON vehicle_registration
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
