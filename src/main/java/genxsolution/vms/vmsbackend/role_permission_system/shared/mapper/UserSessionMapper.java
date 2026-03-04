package genxsolution.vms.vmsbackend.role_permission_system.shared.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserSessionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.UserSession;
import org.springframework.stereotype.Component;

@Component
public class UserSessionMapper {
    public UserSessionResponse toResponse(UserSession m) {
        return new UserSessionResponse(m.session_id(), m.user_id(), m.company_id(), m.company_code(), m.session_token(), m.ip_address(), m.user_agent(), m.created_at(), m.last_activity(), m.expires_at(), m.is_active());
    }
}









