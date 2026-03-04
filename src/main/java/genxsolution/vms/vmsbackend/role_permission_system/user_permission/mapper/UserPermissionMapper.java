package genxsolution.vms.vmsbackend.role_permission_system.user_permission.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.user_permission.dto.UserPermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_permission.model.UserPermission;
import org.springframework.stereotype.Component;

@Component
public class UserPermissionMapper {
    public UserPermissionResponse toResponse(UserPermission m) {
        return new UserPermissionResponse(m.user_permission_id(), m.user_id(), m.permission_id(), m.grant_type(), m.assigned_by(), m.assigned_at());
    }
}










