package genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.dto.UserDataScopeResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.model.UserDataScope;
import org.springframework.stereotype.Component;

@Component
public class UserDataScopeMapper {
    public UserDataScopeResponse toResponse(UserDataScope m) {
        return new UserDataScopeResponse(m.scope_id(), m.user_id(), m.scope_type_id(), m.company_id(), m.company_code(), m.branch_id(), m.department_id(), m.created_at(), m.is_active());
    }
}










