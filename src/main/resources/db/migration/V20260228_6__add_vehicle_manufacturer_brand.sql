ALTER TABLE vehicle_manufacturer
    ADD COLUMN IF NOT EXISTS manufacturer_brand VARCHAR(100);
