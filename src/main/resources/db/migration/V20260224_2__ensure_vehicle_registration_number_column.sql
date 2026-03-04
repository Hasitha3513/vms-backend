-- Align older vehicle table with backend/API expectations.
-- Some DBs were created before registration_number was added to vehicle.

ALTER TABLE vehicle
    ADD COLUMN IF NOT EXISTS registration_number VARCHAR(50);

-- Keep uniqueness semantics for non-null registrations.
CREATE UNIQUE INDEX IF NOT EXISTS ux_vehicle_registration_number
    ON vehicle (registration_number)
    WHERE registration_number IS NOT NULL;
