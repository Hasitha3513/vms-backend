package genxsolution.vms.vmsbackend.role_permission_system.shared.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.PasswordResetTokenResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.PasswordResetToken;
import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenMapper {
    public PasswordResetTokenResponse toResponse(PasswordResetToken m) {
        return new PasswordResetTokenResponse(m.token_id(), m.user_id(), m.token_hash(), m.expires_at(), m.used_at(), m.is_used(), m.created_at());
    }
}









