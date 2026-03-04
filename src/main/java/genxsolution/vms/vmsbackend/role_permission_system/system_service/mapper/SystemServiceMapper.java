package genxsolution.vms.vmsbackend.role_permission_system.system_service.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.system_service.dto.SystemServiceResponse;
import genxsolution.vms.vmsbackend.role_permission_system.system_service.model.SystemService;
import org.springframework.stereotype.Component;

@Component
public class SystemServiceMapper {
    public SystemServiceResponse toResponse(SystemService m) {
        return new SystemServiceResponse(m.service_id(), m.module_id(), m.service_code(), m.service_name(), m.service_description(), m.service_path(), m.icon_name(), m.parent_service_id(), m.display_order(), m.is_active());
    }
}










