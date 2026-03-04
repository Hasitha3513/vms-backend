package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record ActiveSessionResponse(UUID sessionId, UUID userId, String username, String companyCode, String ipAddress, String userAgent, Instant createdAt, Instant lastActivity, Instant expiresAt) {}







