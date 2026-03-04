package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record LoginResponse(UUID userId, String username, String companyCode, String employeeName, String sessionToken, Instant expiresAt, boolean superAdmin, boolean companyAdmin) {}







