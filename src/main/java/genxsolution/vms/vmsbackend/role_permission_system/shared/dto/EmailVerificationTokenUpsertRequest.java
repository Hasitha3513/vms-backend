package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

public record EmailVerificationTokenUpsertRequest(
        Object user_id,
        Object token_hash,
        Object email,
        Object expires_at,
        Object verified_at,
        Object is_used
) {
}









