package genxsolution.vms.vmsbackend.role_permission_system.shared.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserHistoryResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.UserHistory;
import org.springframework.stereotype.Component;

@Component
public class UserHistoryMapper {
    public UserHistoryResponse toResponse(UserHistory m) {
        return new UserHistoryResponse(m.history_id(), m.user_id(), m.company_id(), m.company_code(), m.action(), m.entity_type(), m.entity_id(), m.old_values(), m.new_values(), m.ip_address(), m.user_agent(), m.created_at());
    }
}









