package genxsolution.vms.vmsbackend.dashboard.model;

import java.time.Instant;
import java.util.UUID;

public record DashboardConfig(
        UUID config_id,
        UUID company_id,
        String company_code,
        UUID user_id,
        UUID role_id,
        String widget_name,
        String widget_type,
        String widget_title,
        Integer position_x,
        Integer position_y,
        Integer width,
        Integer height,
        Boolean is_visible,
        Integer refresh_interval,
        String config_data,
        Instant created_at,
        Instant updated_at
) {
}









