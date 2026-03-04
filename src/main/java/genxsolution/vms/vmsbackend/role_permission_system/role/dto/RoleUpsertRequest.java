package genxsolution.vms.vmsbackend.role_permission_system.role.dto;

public record RoleUpsertRequest(
        Object company_id,
        Object company_code,
        Object role_code,
        Object role_name,
        Object description,
        Object is_system,
        Object is_active
) {
}










