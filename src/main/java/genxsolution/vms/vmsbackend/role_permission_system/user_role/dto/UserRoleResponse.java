package genxsolution.vms.vmsbackend.role_permission_system.user_role.dto;

import java.time.Instant;
import java.util.UUID;

public record UserRoleResponse(
        UUID user_role_id,
        UUID user_id,
        UUID role_id,
        UUID company_id,
        String company_code,
        UUID branch_id,
        UUID department_id,
        Instant assigned_at,
        UUID assigned_by,
        Instant expires_at,
        Boolean is_active
) {
}










