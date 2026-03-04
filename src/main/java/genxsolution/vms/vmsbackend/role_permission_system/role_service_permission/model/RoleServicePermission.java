package genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.model;

import java.util.UUID;

public record RoleServicePermission(
        UUID role_id,
        UUID service_id,
        Boolean can_access,
        Boolean can_create,
        Boolean can_edit,
        Boolean can_delete,
        Boolean can_export
) {
}









