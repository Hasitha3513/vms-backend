package genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.repository;

import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.dto.UserDataScopeUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.model.UserDataScope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserDataScopeRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserDataScopeRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<UserDataScope> findAll() {
        return jdbcTemplate.query("SELECT scope_id, user_id, scope_type_id, company_id, company_code, branch_id, department_id, created_at, is_active FROM user_data_scope ORDER BY scope_id", this::mapRow);
    }
    public Optional<UserDataScope> findById(UUID id) {
        return jdbcTemplate.query("SELECT scope_id, user_id, scope_type_id, company_id, company_code, branch_id, department_id, created_at, is_active FROM user_data_scope WHERE scope_id = ?", this::mapRow, id).stream().findFirst();
    }
    public UserDataScope create(UserDataScopeUpsertRequest r) {
        String sql = "INSERT INTO user_data_scope (user_id, scope_type_id, company_id, company_code, branch_id, department_id, is_active) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING scope_id, user_id, scope_type_id, company_id, company_code, branch_id, department_id, created_at, is_active";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.scope_type_id(), r.company_id(), r.company_code(), r.branch_id(), r.department_id(), r.is_active());
    }
    public Optional<UserDataScope> update(UUID id, UserDataScopeUpsertRequest r) {
        String sql = """
                UPDATE user_data_scope
                SET user_id = COALESCE(?, user_id),
                    scope_type_id = COALESCE(?, scope_type_id),
                    company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    branch_id = COALESCE(?, branch_id),
                    department_id = COALESCE(?, department_id),
                    is_active = COALESCE(?, is_active)
                WHERE scope_id = ?
                RETURNING scope_id, user_id, scope_type_id, company_id, company_code, branch_id, department_id, created_at, is_active
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.scope_type_id(), r.company_id(), r.company_code(), r.branch_id(), r.department_id(), r.is_active(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM user_data_scope WHERE scope_id = ?", id) > 0; }
    private UserDataScope mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserDataScope(
                rs.getObject("scope_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("scope_type_id", java.lang.Integer.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getObject("branch_id", java.util.UUID.class),
                rs.getObject("department_id", java.util.UUID.class),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant(),
                rs.getObject("is_active", java.lang.Boolean.class)
        );
    }
}









