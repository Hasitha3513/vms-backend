package genxsolution.vms.vmsbackend.role_permission_system.role.repository;

import genxsolution.vms.vmsbackend.role_permission_system.role.dto.RoleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role.model.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoleRepository {
    private final JdbcTemplate jdbcTemplate;
    public RoleRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<Role> findAll() {
        return jdbcTemplate.query("SELECT role_id, company_id, company_code, role_code, role_name, description, is_system, is_active, created_at FROM role ORDER BY role_id", this::mapRow);
    }
    public Optional<Role> findById(UUID id) {
        return jdbcTemplate.query("SELECT role_id, company_id, company_code, role_code, role_name, description, is_system, is_active, created_at FROM role WHERE role_id = ?", this::mapRow, id).stream().findFirst();
    }
    public Role create(RoleUpsertRequest r) {
        String sql = "INSERT INTO role (company_id, company_code, role_code, role_name, description, is_system, is_active) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING role_id, company_id, company_code, role_code, role_name, description, is_system, is_active, created_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.company_id(), r.company_code(), r.role_code(), r.role_name(), r.description(), r.is_system(), r.is_active());
    }
    public Optional<Role> update(UUID id, RoleUpsertRequest r) {
        String sql = """
                UPDATE role
                SET company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    role_code = COALESCE(?, role_code),
                    role_name = COALESCE(?, role_name),
                    description = COALESCE(?, description),
                    is_system = COALESCE(?, is_system),
                    is_active = COALESCE(?, is_active)
                WHERE role_id = ?
                RETURNING role_id, company_id, company_code, role_code, role_name, description, is_system, is_active, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.company_id(), r.company_code(), r.role_code(), r.role_name(), r.description(), r.is_system(), r.is_active(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM role WHERE role_id = ?", id) > 0; }
    private Role mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Role(
                rs.getObject("role_id", java.util.UUID.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getString("role_code"),
                rs.getString("role_name"),
                rs.getString("description"),
                rs.getObject("is_system", java.lang.Boolean.class),
                rs.getObject("is_active", java.lang.Boolean.class),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant()
        );
    }
}









