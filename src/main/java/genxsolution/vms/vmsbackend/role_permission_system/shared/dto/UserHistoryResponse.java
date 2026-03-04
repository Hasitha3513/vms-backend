package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record UserHistoryResponse(
        UUID history_id,
        UUID user_id,
        UUID company_id,
        String company_code,
        String action,
        String entity_type,
        UUID entity_id,
        String old_values,
        String new_values,
        String ip_address,
        String user_agent,
        Instant created_at
) {
}









