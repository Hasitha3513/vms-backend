package genxsolution.vms.vmsbackend.role_permission_system.permission.dto;

public record PermissionUpsertRequest(
        Object permission_code,
        Object module_id,
        Object description,
        Object is_active
) {
}










