package genxsolution.vms.vmsbackend.role_permission_system.user_permission.model;

import java.time.Instant;
import java.util.UUID;

public record UserPermission(
        UUID user_permission_id,
        UUID user_id,
        UUID permission_id,
        String grant_type,
        UUID assigned_by,
        Instant assigned_at
) {
}










