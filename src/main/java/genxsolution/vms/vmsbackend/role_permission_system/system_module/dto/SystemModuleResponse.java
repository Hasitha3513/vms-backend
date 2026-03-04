package genxsolution.vms.vmsbackend.role_permission_system.system_module.dto;

import java.time.Instant;
import java.util.UUID;

public record SystemModuleResponse(
        UUID module_id,
        String module_code,
        String module_name,
        String description,
        Integer display_order,
        Boolean is_active
) {
}










