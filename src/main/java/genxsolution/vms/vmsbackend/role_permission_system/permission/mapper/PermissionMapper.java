package genxsolution.vms.vmsbackend.role_permission_system.permission.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.permission.dto.PermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.permission.model.Permission;
import org.springframework.stereotype.Component;

@Component
public class PermissionMapper {
    public PermissionResponse toResponse(Permission m) {
        return new PermissionResponse(m.permission_id(), m.permission_code(), m.module_id(), m.description(), m.is_active());
    }
}










