ALTER TABLE company_vehicles
    ADD COLUMN IF NOT EXISTS companyvehicle_type UUID REFERENCES vehicle_type (type_id),
    ADD COLUMN IF NOT EXISTS companyvehicle_model UUID REFERENCES vehicle_model (model_id);

CREATE INDEX IF NOT EXISTS idx_company_vehicles_type ON company_vehicles (companyvehicle_type);
CREATE INDEX IF NOT EXISTS idx_company_vehicles_model ON company_vehicles (companyvehicle_model);
