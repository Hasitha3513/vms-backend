-- Ensure vehicle.variant_id exists for environments created before model variant support.
-- Safe to run multiple times.

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'vehicle'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'vehicle'
          AND column_name = 'variant_id'
    ) THEN
        ALTER TABLE vehicle
            ADD COLUMN variant_id UUID;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = current_schema()
          AND table_name = 'vehicle'
          AND column_name = 'variant_id'
    ) AND EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'vehicle_model_variant'
    ) AND NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        JOIN information_schema.key_column_usage kcu
          ON tc.constraint_name = kcu.constraint_name
         AND tc.table_schema = kcu.table_schema
        WHERE tc.table_schema = current_schema()
          AND tc.table_name = 'vehicle'
          AND tc.constraint_type = 'FOREIGN KEY'
          AND kcu.column_name = 'variant_id'
    ) THEN
        ALTER TABLE vehicle
            ADD CONSTRAINT fk_vehicle_variant_id
            FOREIGN KEY (variant_id)
            REFERENCES vehicle_model_variant (variant_id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_vehicle_variant_id ON vehicle (variant_id);
