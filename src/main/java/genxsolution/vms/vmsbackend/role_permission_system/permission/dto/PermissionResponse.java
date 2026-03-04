package genxsolution.vms.vmsbackend.role_permission_system.permission.dto;

import java.time.Instant;
import java.util.UUID;

public record PermissionResponse(
        UUID permission_id,
        String permission_code,
        UUID module_id,
        String description,
        Boolean is_active
) {
}










