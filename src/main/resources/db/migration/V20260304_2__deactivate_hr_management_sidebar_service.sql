-- Deactivate legacy HR Management sidebar entry if present.
UPDATE system_service
SET is_active = FALSE
WHERE LOWER(COALESCE(service_path, '')) = '/hr-management'
   OR LOWER(COALESCE(service_code, '')) = 'hr_management';
