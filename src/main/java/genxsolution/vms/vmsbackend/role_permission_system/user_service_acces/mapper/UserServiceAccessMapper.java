package genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.dto.UserServiceAccessResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.model.UserServiceAccess;
import org.springframework.stereotype.Component;

@Component
public class UserServiceAccessMapper {
    public UserServiceAccessResponse toResponse(UserServiceAccess m){ return new UserServiceAccessResponse(m.user_id(), m.service_id(), m.can_access(), m.can_create(), m.can_edit(), m.can_delete(), m.can_export(), m.updated_at()); }
}









