package genxsolution.vms.vmsbackend.role_permission_system.shared.model;

import java.time.Instant;
import java.util.UUID;

public record LoginHistory(
        UUID history_id,
        UUID user_id,
        UUID company_id,
        String company_code,
        String username,
        Instant login_time,
        Instant logout_time,
        String ip_address,
        String user_agent,
        Integer status_id,
        String failure_reason
) {
}









