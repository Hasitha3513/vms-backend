package genxsolution.vms.vmsbackend.role_permission_system.system_service.model;

import java.time.Instant;
import java.util.UUID;

public record SystemService(
        UUID service_id,
        UUID module_id,
        String service_code,
        String service_name,
        String service_description,
        String service_path,
        String icon_name,
        UUID parent_service_id,
        Integer display_order,
        Boolean is_active
) {
}










