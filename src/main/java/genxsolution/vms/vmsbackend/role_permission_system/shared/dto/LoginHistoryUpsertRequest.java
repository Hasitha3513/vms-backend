package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

public record LoginHistoryUpsertRequest(
        Object user_id,
        Object company_id,
        Object company_code,
        Object username,
        Object login_time,
        Object logout_time,
        Object ip_address,
        Object user_agent,
        Object status_id,
        Object failure_reason
) {
}









