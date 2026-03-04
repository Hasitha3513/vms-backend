-- Reactivate / create HR Management and Employee View Details sidebar services when system_service is used.
WITH hr_parent AS (
    SELECT service_id
    FROM system_service
    WHERE LOWER(service_code) = 'hr_management'
       OR LOWER(service_path) = '/hr-management'
    ORDER BY display_order DESC, service_id
    LIMIT 1
)
INSERT INTO system_service (
    service_id, service_code, service_name, service_description, service_path,
    icon_name, parent_service_id, display_order, is_active
)
SELECT
    gen_random_uuid(),
    'HR_MANAGEMENT',
    'HR Management',
    'HR Management workspace',
    '/hr-management',
    'ManageAccountsRoundedIcon',
    NULL,
    120,
    TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM hr_parent
);

UPDATE system_service
SET is_active = TRUE,
    service_name = COALESCE(service_name, 'HR Management'),
    service_path = '/hr-management'
WHERE LOWER(service_code) = 'hr_management'
   OR LOWER(service_path) = '/hr-management';

WITH employee_details_parent AS (
    SELECT service_id
    FROM system_service
    WHERE LOWER(service_code) = 'employee_details'
       OR LOWER(service_path) = '/employee-details'
    ORDER BY display_order DESC, service_id
    LIMIT 1
)
INSERT INTO system_service (
    service_id, service_code, service_name, service_description, service_path,
    icon_name, parent_service_id, display_order, is_active
)
SELECT
    gen_random_uuid(),
    'EMPLOYEE_VIEW_DETAILS',
    'Employee View Details',
    'Detailed employee profile view',
    '/employee-view-details',
    'PersonSearchRoundedIcon',
    edp.service_id,
    125,
    TRUE
FROM employee_details_parent edp
WHERE NOT EXISTS (
    SELECT 1
    FROM system_service ss
    WHERE LOWER(ss.service_code) = 'employee_view_details'
       OR LOWER(ss.service_path) = '/employee-view-details'
);

UPDATE system_service
SET is_active = TRUE,
    service_name = 'Employee View Details',
    service_path = '/employee-view-details'
WHERE LOWER(service_code) = 'employee_view_details'
   OR LOWER(service_path) = '/employee-view-details';
