package genxsolution.vms.vmsbackend.role_permission_system.role.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.role.dto.RoleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {
    public RoleResponse toResponse(Role m) {
        return new RoleResponse(m.role_id(), m.company_id(), m.company_code(), m.role_code(), m.role_name(), m.description(), m.is_system(), m.is_active(), m.created_at());
    }
}










