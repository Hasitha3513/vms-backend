CREATE TABLE IF NOT EXISTS hired_vehicle_registration (
    registration_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    registration_number VARCHAR(50) NOT NULL,
    registration_date DATE,
    registration_expiry DATE,
    registering_authority VARCHAR(150),
    registration_state VARCHAR(100),
    registration_city VARCHAR(100),
    rc_book_number VARCHAR(100),
    rc_status VARCHAR(20) CHECK (rc_status IN ('Valid', 'Expired', 'Suspended')) DEFAULT 'Valid',
    number_plate_type_id INTEGER REFERENCES number_plate_type(plate_type_id),
    renewal_reminder_days INTEGER DEFAULT 30,
    notes TEXT,
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvreg_hiredvehicle ON hired_vehicle_registration(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvreg_expiry ON hired_vehicle_registration(registration_expiry);
DROP TRIGGER IF EXISTS trg_hired_vehicle_registration_upd ON hired_vehicle_registration;
CREATE TRIGGER trg_hired_vehicle_registration_upd BEFORE UPDATE ON hired_vehicle_registration FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS hired_vehicle_insurance (
    insurance_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    insurance_company VARCHAR(150) NOT NULL,
    policy_number VARCHAR(100) UNIQUE NOT NULL,
    insurance_type_id INTEGER REFERENCES insurance_type(ins_type_id),
    policy_start_date DATE,
    policy_expiry_date DATE,
    idv_amount DECIMAL(15,2),
    premium_amount DECIMAL(15,2),
    payment_mode VARCHAR(50),
    payment_date DATE,
    agent_name VARCHAR(100),
    agent_contact VARCHAR(20),
    agent_email VARCHAR(100),
    nominee_name VARCHAR(100),
    add_on_covers TEXT,
    ncb_percent DECIMAL(5,2),
    claim_count INTEGER DEFAULT 0,
    last_claim_date DATE,
    last_claim_amount DECIMAL(15,2),
    renewal_reminder_days INTEGER DEFAULT 30,
    insurance_status VARCHAR(20) DEFAULT 'Active',
    notes TEXT,
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvins_hiredvehicle ON hired_vehicle_insurance(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvins_expiry ON hired_vehicle_insurance(policy_expiry_date);
DROP TRIGGER IF EXISTS trg_hired_vehicle_insurance_upd ON hired_vehicle_insurance;
CREATE TRIGGER trg_hired_vehicle_insurance_upd BEFORE UPDATE ON hired_vehicle_insurance FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS hired_vehicle_fitness_certificate (
    fitness_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    certificate_number VARCHAR(100) NOT NULL,
    issuing_authority VARCHAR(150),
    inspection_center VARCHAR(150),
    inspector_id VARCHAR(100),
    inspector_name VARCHAR(150),
    issue_date DATE,
    expiry_date DATE,
    validity_duration_years INTEGER,
    inspection_result_id INTEGER,
    remarks TEXT,
    renewal_reminder_days INTEGER DEFAULT 30,
    fitness_status VARCHAR(20) DEFAULT 'Valid',
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvfit_hiredvehicle ON hired_vehicle_fitness_certificate(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvfit_expiry ON hired_vehicle_fitness_certificate(expiry_date);
DROP TRIGGER IF EXISTS trg_hired_vehicle_fitness_certificate_upd ON hired_vehicle_fitness_certificate;
CREATE TRIGGER trg_hired_vehicle_fitness_certificate_upd BEFORE UPDATE ON hired_vehicle_fitness_certificate FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE IF NOT EXISTS hired_vehicle_puc (
    puc_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    vehicle_id UUID,
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id) ON DELETE CASCADE,
    hiredvehicle_id UUID NOT NULL REFERENCES hired_vehicles(hiredvehicle_id) ON DELETE CASCADE,
    certificate_number VARCHAR(100) NOT NULL,
    issuing_center VARCHAR(150),
    issue_date DATE,
    expiry_date DATE,
    co_emission_percent DECIMAL(6,2),
    hc_emission_ppm DECIMAL(8,2),
    test_result VARCHAR(20) DEFAULT 'Pass',
    puc_status VARCHAR(20) DEFAULT 'Valid',
    renewal_reminder_days INTEGER DEFAULT 15,
    is_current BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_hvpuc_hiredvehicle ON hired_vehicle_puc(hiredvehicle_id);
CREATE INDEX IF NOT EXISTS idx_hvpuc_expiry ON hired_vehicle_puc(expiry_date);
DROP TRIGGER IF EXISTS trg_hired_vehicle_puc_upd ON hired_vehicle_puc;
CREATE TRIGGER trg_hired_vehicle_puc_upd BEFORE UPDATE ON hired_vehicle_puc FOR EACH ROW EXECUTE FUNCTION set_updated_at();
