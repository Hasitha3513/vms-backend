package genxsolution.vms.vmsbackend.role_permission_system.shared.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.EmailVerificationTokenResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.EmailVerificationToken;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationTokenMapper {
    public EmailVerificationTokenResponse toResponse(EmailVerificationToken m) {
        return new EmailVerificationTokenResponse(m.token_id(), m.user_id(), m.token_hash(), m.email(), m.expires_at(), m.verified_at(), m.is_used(), m.created_at());
    }
}









