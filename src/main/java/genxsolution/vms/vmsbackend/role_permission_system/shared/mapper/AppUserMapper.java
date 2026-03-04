package genxsolution.vms.vmsbackend.role_permission_system.shared.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.AppUser;
import org.springframework.stereotype.Component;

@Component
public class AppUserMapper {
    public AppUserResponse toResponse(AppUser m) {
        return new AppUserResponse(m.user_id(), m.company_id(), m.company_code(), m.employee_id(), m.username(), m.email(), m.password_hash(), m.is_super_admin(), m.is_company_admin(), m.is_active(), m.is_locked(), m.failed_login_attempts(), m.last_login_at(), m.password_changed_at(), m.email_verified(), m.email_verified_at(), m.created_at(), m.updated_at());
    }
}









