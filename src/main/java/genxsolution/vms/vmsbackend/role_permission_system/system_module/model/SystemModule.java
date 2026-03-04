package genxsolution.vms.vmsbackend.role_permission_system.system_module.model;

import java.time.Instant;
import java.util.UUID;

public record SystemModule(
        UUID module_id,
        String module_code,
        String module_name,
        String description,
        Integer display_order,
        Boolean is_active
) {
}










