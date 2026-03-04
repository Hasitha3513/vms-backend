package genxsolution.vms.vmsbackend.role_permission_system.user_role.dto;

public record UserRoleUpsertRequest(
        Object user_id,
        Object role_id,
        Object company_id,
        Object company_code,
        Object branch_id,
        Object department_id,
        Object assigned_by,
        Object expires_at,
        Object is_active
) {
}










