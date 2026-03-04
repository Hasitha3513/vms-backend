CREATE TABLE IF NOT EXISTS hired_vehicles (
    hiredvehicle_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    supplier_id UUID NOT NULL REFERENCES supplier(supplier_id),
    supplier_code VARCHAR,

    hiredvehicle_type UUID REFERENCES vehicle_type(type_id),
    hiredvehicle_model UUID REFERENCES vehicle_model(model_id),
    hiredvehicle_category UUID NOT NULL REFERENCES vehicle_category(category_id),
    hiredvehicle_manufacture UUID NOT NULL REFERENCES vehicle_manufacturer(manufacturer_id),

    registration_number VARCHAR(50) UNIQUE,
    chassis_number VARCHAR(100) UNIQUE NOT NULL,
    engine_number VARCHAR(100) NOT NULL,
    key_number VARCHAR(50),
    vehicle_image TEXT,

    manufacture_year INTEGER NOT NULL,
    color VARCHAR(50),
    fuel_type_id INTEGER REFERENCES fuel_type(type_id),
    transmission_type_id INTEGER REFERENCES transmission_type(type_id),
    number_plate_type_id INTEGER REFERENCES number_plate_type(plate_type_id),
    body_style_id INTEGER REFERENCES body_style(style_id),
    seating_capacity INTEGER,
    undercarriage_type INTEGER REFERENCES undercarriage_type(type_id),
    engine_type INTEGER REFERENCES engine_type(engineType_id),
    engine_manufacture INTEGER REFERENCES engine_manufacture(engineManufacture_id),

    initial_odometer_km DECIMAL(12, 2) DEFAULT 0,
    current_odometer_km DECIMAL(12, 2) DEFAULT 0,
    total_engine_hours DECIMAL(12, 2) DEFAULT 0,

    consumption_method_id INTEGER REFERENCES consumption_method(method_id),
    rated_efficiency_kmpl DECIMAL(8, 3),
    rated_consumption_lph DECIMAL(8, 3),

    ownership_type_id INTEGER REFERENCES ownership_type(type_id) NOT NULL,
    previous_owners_count INTEGER DEFAULT 0,
    manufacture_id UUID REFERENCES vehicle_manufacturer(manufacturer_id),
    distributor_id UUID REFERENCES distributor(distributor_id),
    vehicle_condition VARCHAR(20) CHECK (vehicle_condition IN ('New', 'Used', 'Damaged')) DEFAULT 'New',

    operational_status_id INTEGER REFERENCES operational_status(status_id),
    vehicle_status_id INTEGER REFERENCES vehicle_status(status_id),

    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_by UUID REFERENCES employee(employee_id),
    updated_by UUID REFERENCES employee(employee_id),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_hired_vehicle_supplier ON hired_vehicles(supplier_id);
CREATE INDEX IF NOT EXISTS idx_hired_vehicle_type ON hired_vehicles(hiredvehicle_type);
CREATE INDEX IF NOT EXISTS idx_hired_vehicle_registration ON hired_vehicles(registration_number);
CREATE INDEX IF NOT EXISTS idx_hired_vehicle_chassis ON hired_vehicles(chassis_number);

DROP TRIGGER IF EXISTS trg_hired_vehicles_upd ON hired_vehicles;
CREATE TRIGGER trg_hired_vehicles_upd BEFORE UPDATE ON hired_vehicles FOR EACH ROW EXECUTE FUNCTION set_updated_at();
