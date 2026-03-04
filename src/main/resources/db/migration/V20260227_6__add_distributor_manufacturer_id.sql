ALTER TABLE IF EXISTS distributor
    ADD COLUMN IF NOT EXISTS manufacturer_id UUID;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_distributor_manufacturer'
    ) THEN
        ALTER TABLE distributor
            ADD CONSTRAINT fk_distributor_manufacturer
            FOREIGN KEY (manufacturer_id) REFERENCES vehicle_manufacturer (manufacturer_id);
    END IF;
END $$;
