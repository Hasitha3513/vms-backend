package genxsolution.vms.vmsbackend.role_permission_system.user_permission.repository;

import genxsolution.vms.vmsbackend.role_permission_system.user_permission.dto.UserPermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_permission.model.UserPermission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserPermissionRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserPermissionRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<UserPermission> findAll() {
        return jdbcTemplate.query("SELECT user_permission_id, user_id, permission_id, grant_type, assigned_by, assigned_at FROM user_permission ORDER BY user_permission_id", this::mapRow);
    }
    public Optional<UserPermission> findById(UUID id) {
        return jdbcTemplate.query("SELECT user_permission_id, user_id, permission_id, grant_type, assigned_by, assigned_at FROM user_permission WHERE user_permission_id = ?", this::mapRow, id).stream().findFirst();
    }
    public UserPermission create(UserPermissionUpsertRequest r) {
        String sql = "INSERT INTO user_permission (user_id, permission_id, grant_type, assigned_by) VALUES (?, ?, ?, ?) RETURNING user_permission_id, user_id, permission_id, grant_type, assigned_by, assigned_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.permission_id(), r.grant_type(), r.assigned_by());
    }
    public Optional<UserPermission> update(UUID id, UserPermissionUpsertRequest r) {
        String sql = """
                UPDATE user_permission
                SET user_id = COALESCE(?, user_id),
                    permission_id = COALESCE(?, permission_id),
                    grant_type = COALESCE(?, grant_type),
                    assigned_by = COALESCE(?, assigned_by)
                WHERE user_permission_id = ?
                RETURNING user_permission_id, user_id, permission_id, grant_type, assigned_by, assigned_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.permission_id(), r.grant_type(), r.assigned_by(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM user_permission WHERE user_permission_id = ?", id) > 0; }
    private UserPermission mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserPermission(
                rs.getObject("user_permission_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("permission_id", java.util.UUID.class),
                rs.getString("grant_type"),
                rs.getObject("assigned_by", java.util.UUID.class),
                rs.getTimestamp("assigned_at") == null ? null : rs.getTimestamp("assigned_at").toInstant()
        );
    }
}









