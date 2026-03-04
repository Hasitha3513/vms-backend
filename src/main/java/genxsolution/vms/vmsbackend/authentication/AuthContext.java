package genxsolution.vms.vmsbackend.authentication;

import java.time.Instant;
import java.util.UUID;

public record AuthContext(
        UUID userId,
        String username,
        String companyCode,
        boolean superAdmin,
        boolean companyAdmin,
        UUID sessionId,
        Instant expiresAt
) {
}






