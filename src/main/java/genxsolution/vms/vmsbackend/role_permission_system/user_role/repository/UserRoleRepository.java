package genxsolution.vms.vmsbackend.role_permission_system.user_role.repository;

import genxsolution.vms.vmsbackend.role_permission_system.user_role.dto.UserRoleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_role.model.UserRole;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRoleRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserRoleRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<UserRole> findAll() {
        return jdbcTemplate.query("SELECT user_role_id, user_id, role_id, company_id, company_code, branch_id, department_id, assigned_at, assigned_by, expires_at, is_active FROM user_role ORDER BY user_role_id", this::mapRow);
    }
    public Optional<UserRole> findById(UUID id) {
        return jdbcTemplate.query("SELECT user_role_id, user_id, role_id, company_id, company_code, branch_id, department_id, assigned_at, assigned_by, expires_at, is_active FROM user_role WHERE user_role_id = ?", this::mapRow, id).stream().findFirst();
    }
    public UserRole create(UserRoleUpsertRequest r) {
        String sql = "INSERT INTO user_role (user_id, role_id, company_id, company_code, branch_id, department_id, assigned_by, expires_at, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING user_role_id, user_id, role_id, company_id, company_code, branch_id, department_id, assigned_at, assigned_by, expires_at, is_active";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.role_id(), r.company_id(), r.company_code(), r.branch_id(), r.department_id(), r.assigned_by(), r.expires_at(), r.is_active());
    }
    public Optional<UserRole> update(UUID id, UserRoleUpsertRequest r) {
        String sql = """
                UPDATE user_role
                SET user_id = COALESCE(?, user_id),
                    role_id = COALESCE(?, role_id),
                    company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    branch_id = COALESCE(?, branch_id),
                    department_id = COALESCE(?, department_id),
                    assigned_by = COALESCE(?, assigned_by),
                    expires_at = COALESCE(?, expires_at),
                    is_active = COALESCE(?, is_active)
                WHERE user_role_id = ?
                RETURNING user_role_id, user_id, role_id, company_id, company_code, branch_id, department_id, assigned_at, assigned_by, expires_at, is_active
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.role_id(), r.company_id(), r.company_code(), r.branch_id(), r.department_id(), r.assigned_by(), r.expires_at(), r.is_active(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM user_role WHERE user_role_id = ?", id) > 0; }
    private UserRole mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserRole(
                rs.getObject("user_role_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("role_id", java.util.UUID.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getObject("branch_id", java.util.UUID.class),
                rs.getObject("department_id", java.util.UUID.class),
                rs.getTimestamp("assigned_at") == null ? null : rs.getTimestamp("assigned_at").toInstant(),
                rs.getObject("assigned_by", java.util.UUID.class),
                rs.getTimestamp("expires_at") == null ? null : rs.getTimestamp("expires_at").toInstant(),
                rs.getObject("is_active", java.lang.Boolean.class)
        );
    }
}









