package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

public record AppUserUpsertRequest(
        Object company_id,
        Object company_code,
        Object employee_id,
        Object username,
        Object email,
        Object password_hash,
        Object is_super_admin,
        Object is_company_admin,
        Object is_active,
        Object is_locked,
        Object failed_login_attempts,
        Object last_login_at,
        Object password_changed_at,
        Object email_verified,
        Object email_verified_at
) {
}









