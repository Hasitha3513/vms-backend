package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

import java.time.Instant;
import java.util.UUID;

public record AppUserResponse(
        UUID user_id,
        UUID company_id,
        String company_code,
        UUID employee_id,
        String username,
        String email,
        String password_hash,
        Boolean is_super_admin,
        Boolean is_company_admin,
        Boolean is_active,
        Boolean is_locked,
        Integer failed_login_attempts,
        Instant last_login_at,
        Instant password_changed_at,
        Boolean email_verified,
        Instant email_verified_at,
        Instant created_at,
        Instant updated_at
) {
}









