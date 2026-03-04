package genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.repository;

import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.dto.RoleHierarchyUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.model.RoleHierarchy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoleHierarchyRepository {
    private final JdbcTemplate jdbcTemplate;
    public RoleHierarchyRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<RoleHierarchy> findAll() {
        return jdbcTemplate.query("SELECT hierarchy_id, parent_role_id, child_role_id, created_at FROM role_hierarchy ORDER BY hierarchy_id", this::mapRow);
    }
    public Optional<RoleHierarchy> findById(UUID id) {
        return jdbcTemplate.query("SELECT hierarchy_id, parent_role_id, child_role_id, created_at FROM role_hierarchy WHERE hierarchy_id = ?", this::mapRow, id).stream().findFirst();
    }
    public RoleHierarchy create(RoleHierarchyUpsertRequest r) {
        String sql = "INSERT INTO role_hierarchy (parent_role_id, child_role_id) VALUES (?, ?) RETURNING hierarchy_id, parent_role_id, child_role_id, created_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.parent_role_id(), r.child_role_id());
    }
    public Optional<RoleHierarchy> update(UUID id, RoleHierarchyUpsertRequest r) {
        String sql = """
                UPDATE role_hierarchy
                SET parent_role_id = COALESCE(?, parent_role_id),
                    child_role_id = COALESCE(?, child_role_id)
                WHERE hierarchy_id = ?
                RETURNING hierarchy_id, parent_role_id, child_role_id, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.parent_role_id(), r.child_role_id(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM role_hierarchy WHERE hierarchy_id = ?", id) > 0; }
    private RoleHierarchy mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new RoleHierarchy(
                rs.getObject("hierarchy_id", java.util.UUID.class),
                rs.getObject("parent_role_id", java.util.UUID.class),
                rs.getObject("child_role_id", java.util.UUID.class),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant()
        );
    }
}









