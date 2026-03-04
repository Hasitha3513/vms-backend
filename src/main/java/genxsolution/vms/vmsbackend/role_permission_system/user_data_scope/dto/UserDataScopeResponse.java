package genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.dto;

import java.time.Instant;
import java.util.UUID;

public record UserDataScopeResponse(
        UUID scope_id,
        UUID user_id,
        Integer scope_type_id,
        UUID company_id,
        String company_code,
        UUID branch_id,
        UUID department_id,
        Instant created_at,
        Boolean is_active
) {
}










