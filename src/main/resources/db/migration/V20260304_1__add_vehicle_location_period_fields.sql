ALTER TABLE vehicle_location
    ADD COLUMN IF NOT EXISTS period_start_date DATE,
    ADD COLUMN IF NOT EXISTS period_end_date DATE,
    ADD COLUMN IF NOT EXISTS duration_days INTEGER;

UPDATE vehicle_location
SET duration_days = GREATEST(
        1,
        (COALESCE(period_end_date, CURRENT_DATE) - COALESCE(period_start_date, DATE(recorded_at))) + 1
    )
WHERE duration_days IS NULL
  AND (period_start_date IS NOT NULL OR recorded_at IS NOT NULL);
