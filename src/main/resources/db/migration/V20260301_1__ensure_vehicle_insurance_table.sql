CREATE TABLE IF NOT EXISTS vehicle_insurance (
    insurance_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id              UUID,
    companyvehicle_id       UUID,
    company_id              UUID,
    insurance_company       VARCHAR(150),
    policy_number           VARCHAR(100),
    insurance_type_id       INTEGER,
    policy_start_date       DATE,
    policy_expiry_date      DATE,
    idv_amount              DECIMAL(15, 2),
    premium_amount          DECIMAL(15, 2),
    payment_mode            VARCHAR(50),
    payment_date            DATE,
    agent_name              VARCHAR(150),
    agent_contact           VARCHAR(20),
    agent_email             VARCHAR(100),
    nominee_name            VARCHAR(150),
    add_on_covers           TEXT,
    ncb_percent             DECIMAL(5, 2) DEFAULT 0,
    claim_count             INTEGER DEFAULT 0,
    last_claim_date         DATE,
    last_claim_amount       DECIMAL(15, 2),
    renewal_reminder_days   INTEGER DEFAULT 30,
    insurance_status        VARCHAR(20) DEFAULT 'Active',
    notes                   TEXT,
    is_current              BOOLEAN DEFAULT TRUE,
    created_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS vehicle_id UUID;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS companyvehicle_id UUID;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS company_id UUID;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS insurance_company VARCHAR(150);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS policy_number VARCHAR(100);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS insurance_type_id INTEGER;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS policy_start_date DATE;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS policy_expiry_date DATE;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS idv_amount DECIMAL(15, 2);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS premium_amount DECIMAL(15, 2);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS payment_mode VARCHAR(50);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS payment_date DATE;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS agent_name VARCHAR(150);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS agent_contact VARCHAR(20);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS agent_email VARCHAR(100);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS nominee_name VARCHAR(150);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS add_on_covers TEXT;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS ncb_percent DECIMAL(5, 2) DEFAULT 0;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS claim_count INTEGER DEFAULT 0;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS last_claim_date DATE;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS last_claim_amount DECIMAL(15, 2);
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS renewal_reminder_days INTEGER DEFAULT 30;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS insurance_status VARCHAR(20) DEFAULT 'Active';
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS notes TEXT;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS is_current BOOLEAN DEFAULT TRUE;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE vehicle_insurance ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vins_vehicle'
    ) THEN
        ALTER TABLE vehicle_insurance
            ADD CONSTRAINT fk_vins_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(vehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vins_company'
    ) THEN
        ALTER TABLE vehicle_insurance
            ADD CONSTRAINT fk_vins_company FOREIGN KEY (company_id) REFERENCES company(company_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vins_company_vehicle'
    ) THEN
        ALTER TABLE vehicle_insurance
            ADD CONSTRAINT fk_vins_company_vehicle FOREIGN KEY (companyvehicle_id) REFERENCES company_vehicles(companyvehicle_id) ON DELETE CASCADE;
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vins_insurance_type'
    ) THEN
        ALTER TABLE vehicle_insurance
            ADD CONSTRAINT fk_vins_insurance_type FOREIGN KEY (insurance_type_id) REFERENCES insurance_type(ins_type_id);
    END IF;
END$$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'chk_vins_insurance_status'
    ) THEN
        ALTER TABLE vehicle_insurance
            ADD CONSTRAINT chk_vins_insurance_status CHECK (insurance_status IN ('Active', 'Expired', 'Cancelled'));
    END IF;
END$$;

CREATE UNIQUE INDEX IF NOT EXISTS uq_vins_policy_number ON vehicle_insurance(policy_number);
CREATE INDEX IF NOT EXISTS idx_vins_vehicle ON vehicle_insurance(vehicle_id);
CREATE INDEX IF NOT EXISTS idx_vins_expiry ON vehicle_insurance(policy_expiry_date);
CREATE INDEX IF NOT EXISTS idx_vins_status ON vehicle_insurance(insurance_status);

DROP TRIGGER IF EXISTS trg_vehicle_insurance_upd ON vehicle_insurance;
CREATE TRIGGER trg_vehicle_insurance_upd
    BEFORE UPDATE ON vehicle_insurance
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
