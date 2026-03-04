package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

public record PasswordResetTokenUpsertRequest(
        Object user_id,
        Object token_hash,
        Object expires_at,
        Object used_at,
        Object is_used
) {
}









