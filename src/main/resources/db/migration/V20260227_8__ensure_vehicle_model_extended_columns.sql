ALTER TABLE IF EXISTS vehicle_model
    ADD COLUMN IF NOT EXISTS manufacturer_category_id UUID,
    ADD COLUMN IF NOT EXISTS body_style_id INTEGER,
    ADD COLUMN IF NOT EXISTS fuel_type_id INTEGER,
    ADD COLUMN IF NOT EXISTS engine_capacity_cc INTEGER,
    ADD COLUMN IF NOT EXISTS power_hp INTEGER,
    ADD COLUMN IF NOT EXISTS torque_nm INTEGER,
    ADD COLUMN IF NOT EXISTS number_of_cylinders INTEGER,
    ADD COLUMN IF NOT EXISTS transmission_type_id INTEGER,
    ADD COLUMN IF NOT EXISTS drivetrain_type_id INTEGER,
    ADD COLUMN IF NOT EXISTS seating_capacity INTEGER,
    ADD COLUMN IF NOT EXISTS number_of_doors INTEGER,
    ADD COLUMN IF NOT EXISTS kerb_weight_kg DECIMAL(8, 2),
    ADD COLUMN IF NOT EXISTS gvw_kg DECIMAL(8, 2),
    ADD COLUMN IF NOT EXISTS wheelbase_mm INTEGER,
    ADD COLUMN IF NOT EXISTS fuel_efficiency_kmpl DECIMAL(6, 2),
    ADD COLUMN IF NOT EXISTS launch_year INTEGER,
    ADD COLUMN IF NOT EXISTS description TEXT,
    ADD COLUMN IF NOT EXISTS image_url TEXT,
    ADD COLUMN IF NOT EXISTS is_active BOOLEAN DEFAULT TRUE,
    ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'fk_vehicle_model_manufacturer_category'
    ) THEN
        ALTER TABLE vehicle_model
            ADD CONSTRAINT fk_vehicle_model_manufacturer_category
                FOREIGN KEY (manufacturer_category_id) REFERENCES manufacturer_category (id);
    END IF;
END $$;
