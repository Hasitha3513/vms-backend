ALTER TABLE IF EXISTS vehicle_category
    DROP CONSTRAINT IF EXISTS uk_vehicle_category_company_name_code,
    DROP CONSTRAINT IF EXISTS fk_vehicle_category_company_id,
    DROP CONSTRAINT IF EXISTS fk_vehicle_category_company_code;

ALTER TABLE IF EXISTS vehicle_category
    DROP CONSTRAINT IF EXISTS vehicle_category_category_name_category_code_key;

ALTER TABLE IF EXISTS vehicle_category
    ADD CONSTRAINT vehicle_category_category_name_category_code_key UNIQUE (category_name, category_code);

ALTER TABLE IF EXISTS vehicle_category
    DROP COLUMN IF EXISTS company_id,
    DROP COLUMN IF EXISTS company_code;