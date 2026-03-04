package genxsolution.vms.vmsbackend.role_permission_system.role_permission.model;

import java.util.UUID;

public record RolePermission(
        UUID role_id,
        UUID permission_id,
        String grant_type
) {
}









