package genxsolution.vms.vmsbackend.role_permission_system.role_permission.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.role_permission.dto.RolePermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_permission.model.RolePermission;
import org.springframework.stereotype.Component;

@Component
public class RolePermissionMapper {
    public RolePermissionResponse toResponse(RolePermission m){ return new RolePermissionResponse(m.role_id(), m.permission_id(), m.grant_type()); }
}









