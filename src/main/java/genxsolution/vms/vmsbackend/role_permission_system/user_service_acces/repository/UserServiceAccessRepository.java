package genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.repository;

import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.dto.UserServiceAccessUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.model.UserServiceAccess;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserServiceAccessRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserServiceAccessRepository(JdbcTemplate jdbcTemplate){ this.jdbcTemplate=jdbcTemplate; }
    public List<UserServiceAccess> findAll(){ return jdbcTemplate.query("SELECT user_id, service_id, can_access, can_create, can_edit, can_delete, can_export, updated_at FROM user_service_access", (rs,n)-> new UserServiceAccess(rs.getObject("user_id", UUID.class), rs.getObject("service_id", UUID.class), rs.getObject("can_access", Boolean.class), rs.getObject("can_create", Boolean.class), rs.getObject("can_edit", Boolean.class), rs.getObject("can_delete", Boolean.class), rs.getObject("can_export", Boolean.class), rs.getTimestamp("updated_at").toInstant())); }
    public Optional<UserServiceAccess> findById(UUID userId, UUID serviceId){ return jdbcTemplate.query("SELECT user_id, service_id, can_access, can_create, can_edit, can_delete, can_export, updated_at FROM user_service_access WHERE user_id=? AND service_id=?", (rs,n)-> new UserServiceAccess(rs.getObject("user_id", UUID.class), rs.getObject("service_id", UUID.class), rs.getObject("can_access", Boolean.class), rs.getObject("can_create", Boolean.class), rs.getObject("can_edit", Boolean.class), rs.getObject("can_delete", Boolean.class), rs.getObject("can_export", Boolean.class), rs.getTimestamp("updated_at").toInstant()), userId, serviceId).stream().findFirst(); }
    public UserServiceAccess upsert(UserServiceAccessUpsertRequest r){
        String sql="""
                INSERT INTO user_service_access (user_id, service_id, can_access, can_create, can_edit, can_delete, can_export)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (user_id, service_id) DO UPDATE SET
                    can_access=EXCLUDED.can_access, can_create=EXCLUDED.can_create, can_edit=EXCLUDED.can_edit, can_delete=EXCLUDED.can_delete, can_export=EXCLUDED.can_export, updated_at=CURRENT_TIMESTAMP
                RETURNING user_id, service_id, can_access, can_create, can_edit, can_delete, can_export, updated_at
                """;
        return jdbcTemplate.queryForObject(sql,(rs,n)-> new UserServiceAccess(rs.getObject("user_id", UUID.class), rs.getObject("service_id", UUID.class), rs.getObject("can_access", Boolean.class), rs.getObject("can_create", Boolean.class), rs.getObject("can_edit", Boolean.class), rs.getObject("can_delete", Boolean.class), rs.getObject("can_export", Boolean.class), rs.getTimestamp("updated_at").toInstant()), r.user_id(), r.service_id(), r.can_access(), r.can_create(), r.can_edit(), r.can_delete(), r.can_export());
    }
    public boolean delete(UUID userId, UUID serviceId){ return jdbcTemplate.update("DELETE FROM user_service_access WHERE user_id=? AND service_id=?", userId, serviceId)>0; }
}









