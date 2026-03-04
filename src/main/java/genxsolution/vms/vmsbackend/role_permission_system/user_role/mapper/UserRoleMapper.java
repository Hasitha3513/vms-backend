package genxsolution.vms.vmsbackend.role_permission_system.user_role.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.user_role.dto.UserRoleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_role.model.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserRoleMapper {
    public UserRoleResponse toResponse(UserRole m) {
        return new UserRoleResponse(m.user_role_id(), m.user_id(), m.role_id(), m.company_id(), m.company_code(), m.branch_id(), m.department_id(), m.assigned_at(), m.assigned_by(), m.expires_at(), m.is_active());
    }
}










