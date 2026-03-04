DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = current_schema()
          AND table_name = 'ownership_type'
    ) THEN
        ALTER TABLE ownership_type
            ALTER COLUMN type_code TYPE VARCHAR(50);
    END IF;
END $$;

