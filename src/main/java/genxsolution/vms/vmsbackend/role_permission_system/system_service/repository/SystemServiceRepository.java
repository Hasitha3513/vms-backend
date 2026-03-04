package genxsolution.vms.vmsbackend.role_permission_system.system_service.repository;

import genxsolution.vms.vmsbackend.role_permission_system.system_service.dto.SystemServiceUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.system_service.model.SystemService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SystemServiceRepository {
    private final JdbcTemplate jdbcTemplate;
    public SystemServiceRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<SystemService> findAll() {
        return jdbcTemplate.query("SELECT service_id, module_id, service_code, service_name, service_description, service_path, icon_name, parent_service_id, display_order, is_active FROM system_service ORDER BY service_id", this::mapRow);
    }
    public Optional<SystemService> findById(UUID id) {
        return jdbcTemplate.query("SELECT service_id, module_id, service_code, service_name, service_description, service_path, icon_name, parent_service_id, display_order, is_active FROM system_service WHERE service_id = ?", this::mapRow, id).stream().findFirst();
    }
    public SystemService create(SystemServiceUpsertRequest r) {
        String sql = "INSERT INTO system_service (module_id, service_code, service_name, service_description, service_path, icon_name, parent_service_id, display_order, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING service_id, module_id, service_code, service_name, service_description, service_path, icon_name, parent_service_id, display_order, is_active";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.module_id(), r.service_code(), r.service_name(), r.service_description(), r.service_path(), r.icon_name(), r.parent_service_id(), r.display_order(), r.is_active());
    }
    public Optional<SystemService> update(UUID id, SystemServiceUpsertRequest r) {
        String sql = """
                UPDATE system_service
                SET module_id = COALESCE(?, module_id),
                    service_code = COALESCE(?, service_code),
                    service_name = COALESCE(?, service_name),
                    service_description = COALESCE(?, service_description),
                    service_path = COALESCE(?, service_path),
                    icon_name = COALESCE(?, icon_name),
                    parent_service_id = COALESCE(?, parent_service_id),
                    display_order = COALESCE(?, display_order),
                    is_active = COALESCE(?, is_active)
                WHERE service_id = ?
                RETURNING service_id, module_id, service_code, service_name, service_description, service_path, icon_name, parent_service_id, display_order, is_active
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.module_id(), r.service_code(), r.service_name(), r.service_description(), r.service_path(), r.icon_name(), r.parent_service_id(), r.display_order(), r.is_active(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM system_service WHERE service_id = ?", id) > 0; }
    private SystemService mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new SystemService(
                rs.getObject("service_id", java.util.UUID.class),
                rs.getObject("module_id", java.util.UUID.class),
                rs.getString("service_code"),
                rs.getString("service_name"),
                rs.getString("service_description"),
                rs.getString("service_path"),
                rs.getString("icon_name"),
                rs.getObject("parent_service_id", java.util.UUID.class),
                rs.getObject("display_order", java.lang.Integer.class),
                rs.getObject("is_active", java.lang.Boolean.class)
        );
    }
}









