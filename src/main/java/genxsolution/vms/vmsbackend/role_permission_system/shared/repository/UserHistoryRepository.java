package genxsolution.vms.vmsbackend.role_permission_system.shared.repository;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserHistoryUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.UserHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserHistoryRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserHistoryRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<UserHistory> findAll() {
        return jdbcTemplate.query("SELECT history_id, user_id, company_id, company_code, action, entity_type, entity_id, old_values, new_values, ip_address, user_agent, created_at FROM user_history ORDER BY history_id", this::mapRow);
    }
    public Optional<UserHistory> findById(UUID id) {
        return jdbcTemplate.query("SELECT history_id, user_id, company_id, company_code, action, entity_type, entity_id, old_values, new_values, ip_address, user_agent, created_at FROM user_history WHERE history_id = ?", this::mapRow, id).stream().findFirst();
    }
    public UserHistory create(UserHistoryUpsertRequest r) {
        String sql = "INSERT INTO user_history (user_id, company_id, company_code, action, entity_type, entity_id, old_values, new_values, ip_address, user_agent) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING history_id, user_id, company_id, company_code, action, entity_type, entity_id, old_values, new_values, ip_address, user_agent, created_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.company_id(), r.company_code(), r.action(), r.entity_type(), r.entity_id(), r.old_values(), r.new_values(), r.ip_address(), r.user_agent());
    }
    public Optional<UserHistory> update(UUID id, UserHistoryUpsertRequest r) {
        String sql = """
                UPDATE user_history
                SET user_id = COALESCE(?, user_id),
                    company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    action = COALESCE(?, action),
                    entity_type = COALESCE(?, entity_type),
                    entity_id = COALESCE(?, entity_id),
                    old_values = COALESCE(?, old_values),
                    new_values = COALESCE(?, new_values),
                    ip_address = COALESCE(?, ip_address),
                    user_agent = COALESCE(?, user_agent)
                WHERE history_id = ?
                RETURNING history_id, user_id, company_id, company_code, action, entity_type, entity_id, old_values, new_values, ip_address, user_agent, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.company_id(), r.company_code(), r.action(), r.entity_type(), r.entity_id(), r.old_values(), r.new_values(), r.ip_address(), r.user_agent(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM user_history WHERE history_id = ?", id) > 0; }
    private UserHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserHistory(
                rs.getObject("history_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getString("action"),
                rs.getString("entity_type"),
                rs.getObject("entity_id", java.util.UUID.class),
                rs.getString("old_values"),
                rs.getString("new_values"),
                rs.getString("ip_address"),
                rs.getString("user_agent"),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant()
        );
    }
}








