package genxsolution.vms.vmsbackend.role_permission_system.role_permission.repository;

import genxsolution.vms.vmsbackend.role_permission_system.role_permission.dto.RolePermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_permission.model.RolePermission;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RolePermissionRepository {
    private final JdbcTemplate jdbcTemplate;
    public RolePermissionRepository(JdbcTemplate jdbcTemplate){ this.jdbcTemplate=jdbcTemplate; }
    public List<RolePermission> findAll(){ return jdbcTemplate.query("SELECT role_id, permission_id, grant_type FROM role_permission", (rs,n)-> new RolePermission(rs.getObject("role_id", UUID.class), rs.getObject("permission_id", UUID.class), rs.getString("grant_type"))); }
    public Optional<RolePermission> findById(UUID roleId, UUID permissionId){ return jdbcTemplate.query("SELECT role_id, permission_id, grant_type FROM role_permission WHERE role_id=? AND permission_id=?", (rs,n)-> new RolePermission(rs.getObject("role_id", UUID.class), rs.getObject("permission_id", UUID.class), rs.getString("grant_type")), roleId, permissionId).stream().findFirst(); }
    public RolePermission upsert(RolePermissionUpsertRequest r){
        String sql="""
                INSERT INTO role_permission (role_id, permission_id, grant_type)
                VALUES (?, ?, COALESCE(?, 'GRANT'))
                ON CONFLICT (role_id, permission_id) DO UPDATE SET grant_type = EXCLUDED.grant_type
                RETURNING role_id, permission_id, grant_type
                """;
        return jdbcTemplate.queryForObject(sql,(rs,n)-> new RolePermission(rs.getObject("role_id", UUID.class), rs.getObject("permission_id", UUID.class), rs.getString("grant_type")), r.role_id(), r.permission_id(), r.grant_type());
    }
    public boolean delete(UUID roleId, UUID permissionId){ return jdbcTemplate.update("DELETE FROM role_permission WHERE role_id=? AND permission_id=?", roleId, permissionId)>0; }
}









