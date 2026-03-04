package genxsolution.vms.vmsbackend.dashboard.mapper;

import genxsolution.vms.vmsbackend.dashboard.dto.DashboardConfigResponse;
import genxsolution.vms.vmsbackend.dashboard.model.DashboardConfig;
import org.springframework.stereotype.Component;

@Component
public class DashboardConfigMapper {
    public DashboardConfigResponse toResponse(DashboardConfig m) {
        return new DashboardConfigResponse(m.config_id(), m.company_id(), m.company_code(), m.user_id(), m.role_id(), m.widget_name(), m.widget_type(), m.widget_title(), m.position_x(), m.position_y(), m.width(), m.height(), m.is_visible(), m.refresh_interval(), m.config_data(), m.created_at(), m.updated_at());
    }
}









