package genxsolution.vms.vmsbackend.role_permission_system.shared.model;

import java.time.Instant;
import java.util.UUID;

public record UserSession(
        UUID session_id,
        UUID user_id,
        UUID company_id,
        String company_code,
        String session_token,
        String ip_address,
        String user_agent,
        Instant created_at,
        Instant last_activity,
        Instant expires_at,
        Boolean is_active
) {
}









