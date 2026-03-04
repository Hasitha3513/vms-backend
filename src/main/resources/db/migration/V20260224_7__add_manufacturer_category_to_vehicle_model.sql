ALTER TABLE IF EXISTS vehicle_model
    ADD COLUMN IF NOT EXISTS manufacturer_category_id UUID REFERENCES manufacturer_category (id);
