ALTER TABLE company_vehicles
    ADD COLUMN IF NOT EXISTS current_ownership VARCHAR(200);

