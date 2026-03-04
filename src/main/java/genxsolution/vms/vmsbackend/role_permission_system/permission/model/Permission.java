package genxsolution.vms.vmsbackend.role_permission_system.permission.model;

import java.time.Instant;
import java.util.UUID;

public record Permission(
        UUID permission_id,
        String permission_code,
        UUID module_id,
        String description,
        Boolean is_active
) {
}










