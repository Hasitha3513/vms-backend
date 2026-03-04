WITH upsert_module AS (
    INSERT INTO system_module (module_code, module_name, description, display_order, is_active)
    VALUES ('SUPPLIER_MANAGEMENT', 'Supplier Management', 'Supplier management navigation module', 150, TRUE)
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
    WHERE sm.module_code = 'SUPPLIER_MANAGEMENT'
    LIMIT 1
),
parent_service AS (
    INSERT INTO system_service (
        module_id, service_code, service_name, service_description,
        service_path, icon_name, parent_service_id, display_order, is_active
    )
    SELECT
        mr.module_id,
        'SUPPLIER_MANAGEMENT_MENU',
        'Supplier Management',
        'Supplier management root menu',
        '/supplier-management',
        'store',
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
    WHERE ss.service_code = 'SUPPLIER_MANAGEMENT_MENU'
    LIMIT 1
),
upsert_overview AS (
    INSERT INTO system_service (
        module_id, service_code, service_name, service_description,
        service_path, icon_name, parent_service_id, display_order, is_active
    )
    SELECT
        mr.module_id,
        'SUPPLIER_OVERVIEW_TAB',
        'Supplier Overview',
        'Supplier overview tab',
        '/supplier-management/overview',
        'bar_chart',
        pr.service_id,
        1,
        TRUE
    FROM module_ref mr
    CROSS JOIN parent_ref pr
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
upsert_details AS (
    INSERT INTO system_service (
        module_id, service_code, service_name, service_description,
        service_path, icon_name, parent_service_id, display_order, is_active
    )
    SELECT
        mr.module_id,
        'SUPPLIER_DETAILS_TAB',
        'Supplier Details',
        'Supplier details tab',
        '/supplier-management/details',
        'description',
        pr.service_id,
        2,
        TRUE
    FROM module_ref mr
    CROSS JOIN parent_ref pr
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
    SELECT service_id FROM upsert_overview
    UNION ALL
    SELECT service_id FROM upsert_details
    UNION ALL
    SELECT ss.service_id FROM system_service ss WHERE ss.service_code IN ('SUPPLIER_OVERVIEW_TAB', 'SUPPLIER_DETAILS_TAB')
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
