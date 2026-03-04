package genxsolution.vms.vmsbackend.role_permission_system.shared.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.LoginHistoryResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.LoginHistory;
import org.springframework.stereotype.Component;

@Component
public class LoginHistoryMapper {
    public LoginHistoryResponse toResponse(LoginHistory m) {
        return new LoginHistoryResponse(m.history_id(), m.user_id(), m.company_id(), m.company_code(), m.username(), m.login_time(), m.logout_time(), m.ip_address(), m.user_agent(), m.status_id(), m.failure_reason());
    }
}









