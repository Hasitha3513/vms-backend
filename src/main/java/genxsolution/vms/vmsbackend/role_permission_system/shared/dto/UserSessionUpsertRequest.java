package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

public record UserSessionUpsertRequest(
        Object user_id,
        Object company_id,
        Object company_code,
        Object session_token,
        Object ip_address,
        Object user_agent,
        Object last_activity,
        Object expires_at,
        Object is_active
) {
}









