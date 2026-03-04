package genxsolution.vms.vmsbackend.role_permission_system.role.dto;

import java.time.Instant;
import java.util.UUID;

public record RoleResponse(
        UUID role_id,
        UUID company_id,
        String company_code,
        String role_code,
        String role_name,
        String description,
        Boolean is_system,
        Boolean is_active,
        Instant created_at
) {
}










