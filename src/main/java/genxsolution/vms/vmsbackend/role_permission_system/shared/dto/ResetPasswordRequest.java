package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

import java.util.UUID;

public record ResetPasswordRequest(UUID userId, String newPassword) {}







