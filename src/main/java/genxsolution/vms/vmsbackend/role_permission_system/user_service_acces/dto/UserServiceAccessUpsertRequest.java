package genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.dto;

import java.util.UUID;

public record UserServiceAccessUpsertRequest(UUID user_id, UUID service_id, Boolean can_access, Boolean can_create, Boolean can_edit, Boolean can_delete, Boolean can_export) {}









