package genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.dto.RoleServicePermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.model.RoleServicePermission;
import org.springframework.stereotype.Component;

@Component
public class RoleServicePermissionMapper {
    public RoleServicePermissionResponse toResponse(RoleServicePermission m){ return new RoleServicePermissionResponse(m.role_id(), m.service_id(), m.can_access(), m.can_create(), m.can_edit(), m.can_delete(), m.can_export()); }
}









