package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

public record UserHistoryUpsertRequest(
        Object user_id,
        Object company_id,
        Object company_code,
        Object action,
        Object entity_type,
        Object entity_id,
        Object old_values,
        Object new_values,
        Object ip_address,
        Object user_agent
) {
}









