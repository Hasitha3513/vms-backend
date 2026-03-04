package genxsolution.vms.vmsbackend.role_permission_system.system_module.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.system_module.dto.SystemModuleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.system_module.model.SystemModule;
import org.springframework.stereotype.Component;

@Component
public class SystemModuleMapper {
    public SystemModuleResponse toResponse(SystemModule m) {
        return new SystemModuleResponse(m.module_id(), m.module_code(), m.module_name(), m.description(), m.display_order(), m.is_active());
    }
}










