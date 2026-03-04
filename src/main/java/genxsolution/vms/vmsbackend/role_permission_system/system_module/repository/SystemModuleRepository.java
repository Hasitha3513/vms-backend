package genxsolution.vms.vmsbackend.role_permission_system.system_module.repository;

import genxsolution.vms.vmsbackend.role_permission_system.system_module.dto.SystemModuleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.system_module.model.SystemModule;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SystemModuleRepository {
    private final JdbcTemplate jdbcTemplate;
    public SystemModuleRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<SystemModule> findAll() {
        return jdbcTemplate.query("SELECT module_id, module_code, module_name, description, display_order, is_active FROM system_module ORDER BY module_id", this::mapRow);
    }
    public Optional<SystemModule> findById(UUID id) {
        return jdbcTemplate.query("SELECT module_id, module_code, module_name, description, display_order, is_active FROM system_module WHERE module_id = ?", this::mapRow, id).stream().findFirst();
    }
    public SystemModule create(SystemModuleUpsertRequest r) {
        String sql = "INSERT INTO system_module (module_code, module_name, description, display_order, is_active) VALUES (?, ?, ?, ?, ?) RETURNING module_id, module_code, module_name, description, display_order, is_active";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.module_code(), r.module_name(), r.description(), r.display_order(), r.is_active());
    }
    public Optional<SystemModule> update(UUID id, SystemModuleUpsertRequest r) {
        String sql = """
                UPDATE system_module
                SET module_code = COALESCE(?, module_code),
                    module_name = COALESCE(?, module_name),
                    description = COALESCE(?, description),
                    display_order = COALESCE(?, display_order),
                    is_active = COALESCE(?, is_active)
                WHERE module_id = ?
                RETURNING module_id, module_code, module_name, description, display_order, is_active
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.module_code(), r.module_name(), r.description(), r.display_order(), r.is_active(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM system_module WHERE module_id = ?", id) > 0; }
    private SystemModule mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SystemModule(
                rs.getObject("module_id", java.util.UUID.class),
                rs.getString("module_code"),
                rs.getString("module_name"),
                rs.getString("description"),
                rs.getObject("display_order", java.lang.Integer.class),
                rs.getObject("is_active", java.lang.Boolean.class)
        );
    }
}









