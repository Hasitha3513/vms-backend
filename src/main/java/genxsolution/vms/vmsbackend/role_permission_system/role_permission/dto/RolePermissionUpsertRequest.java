package genxsolution.vms.vmsbackend.role_permission_system.role_permission.dto;

import java.util.UUID;

public record RolePermissionUpsertRequest(UUID role_id, UUID permission_id, String grant_type) {}









