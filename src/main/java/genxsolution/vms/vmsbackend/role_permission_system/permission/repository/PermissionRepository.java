package genxsolution.vms.vmsbackend.role_permission_system.permission.repository;

import genxsolution.vms.vmsbackend.role_permission_system.permission.dto.PermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.permission.model.Permission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PermissionRepository {
    private final JdbcTemplate jdbcTemplate;
    public PermissionRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<Permission> findAll() {
        return jdbcTemplate.query("SELECT permission_id, permission_code, module_id, description, is_active FROM permission ORDER BY permission_id", this::mapRow);
    }
    public Optional<Permission> findById(UUID id) {
        return jdbcTemplate.query("SELECT permission_id, permission_code, module_id, description, is_active FROM permission WHERE permission_id = ?", this::mapRow, id).stream().findFirst();
    }
    public Permission create(PermissionUpsertRequest r) {
        String sql = "INSERT INTO permission (permission_code, module_id, description, is_active) VALUES (?, ?, ?, ?) RETURNING permission_id, permission_code, module_id, description, is_active";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.permission_code(), r.module_id(), r.description(), r.is_active());
    }
    public Optional<Permission> update(UUID id, PermissionUpsertRequest r) {
        String sql = """
                UPDATE permission
                SET permission_code = COALESCE(?, permission_code),
                    module_id = COALESCE(?, module_id),
                    description = COALESCE(?, description),
                    is_active = COALESCE(?, is_active)
                WHERE permission_id = ?
                RETURNING permission_id, permission_code, module_id, description, is_active
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.permission_code(), r.module_id(), r.description(), r.is_active(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM permission WHERE permission_id = ?", id) > 0; }
    private Permission mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Permission(
                rs.getObject("permission_id", java.util.UUID.class),
                rs.getString("permission_code"),
                rs.getObject("module_id", java.util.UUID.class),
                rs.getString("description"),
                rs.getObject("is_active", java.lang.Boolean.class)
        );
    }
}









