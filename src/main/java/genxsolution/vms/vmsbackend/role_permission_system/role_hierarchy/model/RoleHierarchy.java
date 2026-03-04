package genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.model;

import java.time.Instant;
import java.util.UUID;

public record RoleHierarchy(
        UUID hierarchy_id,
        UUID parent_role_id,
        UUID child_role_id,
        Instant created_at
) {
}










