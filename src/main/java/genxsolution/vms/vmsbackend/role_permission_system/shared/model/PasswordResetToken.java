package genxsolution.vms.vmsbackend.role_permission_system.shared.model;

import java.time.Instant;
import java.util.UUID;

public record PasswordResetToken(
        UUID token_id,
        UUID user_id,
        String token_hash,
        Instant expires_at,
        Instant used_at,
        Boolean is_used,
        Instant created_at
) {
}









