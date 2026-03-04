package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record EmailVerificationTokenResponse(
        UUID token_id,
        UUID user_id,
        String token_hash,
        String email,
        Instant expires_at,
        Instant verified_at,
        Boolean is_used,
        Instant created_at
) {
}









