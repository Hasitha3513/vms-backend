package genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.dto;

import java.time.Instant;
import java.util.UUID;

public record RoleHierarchyResponse(
        UUID hierarchy_id,
        UUID parent_role_id,
        UUID child_role_id,
        Instant created_at
) {
}










