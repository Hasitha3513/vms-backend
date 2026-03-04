package genxsolution.vms.vmsbackend.role_permission_system.user_permission.dto;

public record UserPermissionUpsertRequest(
        Object user_id,
        Object permission_id,
        Object grant_type,
        Object assigned_by
) {
}










