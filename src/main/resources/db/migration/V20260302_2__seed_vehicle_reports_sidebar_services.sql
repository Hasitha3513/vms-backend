WITH upsert_module AS (
    INSERT INTO system_module (module_code, module_name, description, display_order, is_active)
    VALUES ('VEHICLE_REPORTS', 'Vehicle Reports', 'Vehicle reporting navigation module', 160, TRUE)
    ON CONFLICT (module_code) DO UPDATE
        SET module_name = EXCLUDED.module_name,
            description = EXCLUDED.description,
            display_order = EXCLUDED.display_order,
            is_active = TRUE
    RETURNING module_id
),
module_ref AS (
    SELECT module_id FROM upsert_module
    UNION ALL
    SELECT sm.module_id
    FROM system_module sm
    WHERE sm.module_code = 'VEHICLE_REPORTS'
    LIMIT 1
),
parent_service AS (
    INSERT INTO system_service (
        module_id, service_code, service_name, service_description,
        service_path, icon_name, parent_service_id, display_order, is_active
    )
    SELECT
        mr.module_id,
        'VEHICLE_REPORTS_MENU',
        'Vehicle Reports',
        'Vehicle reports root menu',
        '/vehicle-reports',
        'assessment',
        NULL,
        1,
        TRUE
    FROM module_ref mr
    ON CONFLICT (service_code) DO UPDATE
        SET module_id = EXCLUDED.module_id,
            service_name = EXCLUDED.service_name,
            service_description = EXCLUDED.service_description,
            service_path = EXCLUDED.service_path,
            icon_name = EXCLUDED.icon_name,
            parent_service_id = NULL,
            display_order = EXCLUDED.display_order,
            is_active = TRUE
    RETURNING service_id
),
parent_ref AS (
    SELECT service_id FROM parent_service
    UNION ALL
    SELECT ss.service_id
    FROM system_service ss
    WHERE ss.service_code = 'VEHICLE_REPORTS_MENU'
    LIMIT 1
),
child_services AS (
    SELECT *
    FROM (
        VALUES
            ('VEHICLE_REPORT_DASHBOARD_TAB', 'Dashboard', 'Vehicle reports dashboard tab', '/vehicle-reports/dashboard', 'dashboard', 1),
            ('VEHICLE_REPORT_LICENSE_TAB', 'Vehicle License', 'Vehicle license report tab', '/vehicle-reports/license', 'description', 2),
            ('VEHICLE_REPORT_INSURANCE_TAB', 'Vehicle Insurance', 'Vehicle insurance report tab', '/vehicle-reports/insurance', 'description', 3),
            ('VEHICLE_REPORT_FITNESS_TAB', 'Vehicle Fitness', 'Vehicle fitness report tab', '/vehicle-reports/fitness', 'description', 4),
            ('VEHICLE_REPORT_EMISSION_TAB', 'Vehicle Emission Details', 'Vehicle emission report tab', '/vehicle-reports/emission', 'description', 5),
            ('VEHICLE_REPORT_COMPANY_WISE_TAB', 'Company Wise Vehicle Report', 'Company wise vehicle report tab', '/vehicle-reports/company-wise', 'business', 6),
            ('VEHICLE_REPORT_PROJECT_WISE_TAB', 'Project Wise Vehicle Report', 'Project wise vehicle report tab', '/vehicle-reports/project-wise', 'folder', 7)
    ) AS t(service_code, service_name, service_description, service_path, icon_name, display_order)
),
upsert_children AS (
    INSERT INTO system_service (
        module_id, service_code, service_name, service_description,
        service_path, icon_name, parent_service_id, display_order, is_active
    )
    SELECT
        mr.module_id,
        cs.service_code,
        cs.service_name,
        cs.service_description,
        cs.service_path,
        cs.icon_name,
        pr.service_id,
        cs.display_order,
        TRUE
    FROM module_ref mr
    CROSS JOIN parent_ref pr
    CROSS JOIN child_services cs
    ON CONFLICT (service_code) DO UPDATE
        SET module_id = EXCLUDED.module_id,
            service_name = EXCLUDED.service_name,
            service_description = EXCLUDED.service_description,
            service_path = EXCLUDED.service_path,
            icon_name = EXCLUDED.icon_name,
            parent_service_id = EXCLUDED.parent_service_id,
            display_order = EXCLUDED.display_order,
            is_active = TRUE
    RETURNING service_id
),
target_services AS (
    SELECT service_id FROM parent_ref
    UNION ALL
    SELECT service_id FROM upsert_children
    UNION ALL
    SELECT ss.service_id
    FROM system_service ss
    WHERE ss.service_code IN (
        'VEHICLE_REPORT_DASHBOARD_TAB',
        'VEHICLE_REPORT_LICENSE_TAB',
        'VEHICLE_REPORT_INSURANCE_TAB',
        'VEHICLE_REPORT_FITNESS_TAB',
        'VEHICLE_REPORT_EMISSION_TAB',
        'VEHICLE_REPORT_COMPANY_WISE_TAB',
        'VEHICLE_REPORT_PROJECT_WISE_TAB'
    )
),
grant_system_roles AS (
    INSERT INTO role_service_permission (role_id, service_id, can_access, can_create, can_edit, can_delete, can_export)
    SELECT
        r.role_id,
        ts.service_id,
        TRUE, TRUE, TRUE, TRUE, TRUE
    FROM role r
    CROSS JOIN target_services ts
    WHERE COALESCE(r.is_active, TRUE) = TRUE
      AND (COALESCE(r.is_system, FALSE) = TRUE OR UPPER(COALESCE(r.role_code, '')) IN ('SUPER_ADMIN', 'COMPANY_ADMIN', 'ADMIN'))
    ON CONFLICT (role_id, service_id) DO UPDATE
        SET can_access = TRUE,
            can_create = TRUE,
            can_edit = TRUE,
            can_delete = TRUE,
            can_export = TRUE
    RETURNING role_id
)
INSERT INTO user_service_access (user_id, service_id, can_access, can_create, can_edit, can_delete, can_export, updated_at)
SELECT
    u.user_id,
    ts.service_id,
    TRUE, TRUE, TRUE, TRUE, TRUE,
    CURRENT_TIMESTAMP
FROM app_user u
CROSS JOIN target_services ts
WHERE COALESCE(u.is_active, TRUE) = TRUE
  AND (COALESCE(u.is_super_admin, FALSE) = TRUE OR COALESCE(u.is_company_admin, FALSE) = TRUE)
ON CONFLICT (user_id, service_id) DO UPDATE
    SET can_access = TRUE,
        can_create = TRUE,
        can_edit = TRUE,
        can_delete = TRUE,
        can_export = TRUE,
        updated_at = CURRENT_TIMESTAMP;

