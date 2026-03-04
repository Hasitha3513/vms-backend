package genxsolution.vms.vmsbackend.dashboard.dto;

public record DashboardConfigUpsertRequest(
        Object company_id,
        Object company_code,
        Object user_id,
        Object role_id,
        Object widget_name,
        Object widget_type,
        Object widget_title,
        Object position_x,
        Object position_y,
        Object width,
        Object height,
        Object is_visible,
        Object refresh_interval,
        Object config_data
) {
}









