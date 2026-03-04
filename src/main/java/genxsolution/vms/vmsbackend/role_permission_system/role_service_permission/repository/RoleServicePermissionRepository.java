package genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.repository;

import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.dto.RoleServicePermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.model.RoleServicePermission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoleServicePermissionRepository {
    private final JdbcTemplate jdbcTemplate;
    public RoleServicePermissionRepository(JdbcTemplate jdbcTemplate){ this.jdbcTemplate=jdbcTemplate; }
    public List<RoleServicePermission> findAll(){ return jdbcTemplate.query("SELECT role_id, service_id, can_access, can_create, can_edit, can_delete, can_export FROM role_service_permission", (rs,n)-> new RoleServicePermission(rs.getObject("role_id", UUID.class), rs.getObject("service_id", UUID.class), rs.getObject("can_access", Boolean.class), rs.getObject("can_create", Boolean.class), rs.getObject("can_edit", Boolean.class), rs.getObject("can_delete", Boolean.class), rs.getObject("can_export", Boolean.class))); }
    public Optional<RoleServicePermission> findById(UUID roleId, UUID serviceId){ return jdbcTemplate.query("SELECT role_id, service_id, can_access, can_create, can_edit, can_delete, can_export FROM role_service_permission WHERE role_id=? AND service_id=?", (rs,n)-> new RoleServicePermission(rs.getObject("role_id", UUID.class), rs.getObject("service_id", UUID.class), rs.getObject("can_access", Boolean.class), rs.getObject("can_create", Boolean.class), rs.getObject("can_edit", Boolean.class), rs.getObject("can_delete", Boolean.class), rs.getObject("can_export", Boolean.class)), roleId, serviceId).stream().findFirst(); }
    public RoleServicePermission upsert(RoleServicePermissionUpsertRequest r){
        String sql="""
                INSERT INTO role_service_permission (role_id, service_id, can_access, can_create, can_edit, can_delete, can_export)
                VALUES (?, ?, COALESCE(?, FALSE), COALESCE(?, FALSE), COALESCE(?, FALSE), COALESCE(?, FALSE), COALESCE(?, FALSE))
                ON CONFLICT (role_id, service_id) DO UPDATE SET
                    can_access=EXCLUDED.can_access, can_create=EXCLUDED.can_create, can_edit=EXCLUDED.can_edit, can_delete=EXCLUDED.can_delete, can_export=EXCLUDED.can_export
                RETURNING role_id, service_id, can_access, can_create, can_edit, can_delete, can_export
                """;
        return jdbcTemplate.queryForObject(sql,(rs,n)-> new RoleServicePermission(rs.getObject("role_id", UUID.class), rs.getObject("service_id", UUID.class), rs.getObject("can_access", Boolean.class), rs.getObject("can_create", Boolean.class), rs.getObject("can_edit", Boolean.class), rs.getObject("can_delete", Boolean.class), rs.getObject("can_export", Boolean.class)), r.role_id(), r.service_id(), r.can_access(), r.can_create(), r.can_edit(), r.can_delete(), r.can_export());
    }
    public boolean delete(UUID roleId, UUID serviceId){ return jdbcTemplate.update("DELETE FROM role_service_permission WHERE role_id=? AND service_id=?", roleId, serviceId)>0; }
}









