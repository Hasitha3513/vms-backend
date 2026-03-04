package genxsolution.vms.vmsbackend.role_permission_system.shared.repository;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.LoginHistoryUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.LoginHistory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LoginHistoryRepository {
    private final JdbcTemplate jdbcTemplate;
    public LoginHistoryRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<LoginHistory> findAll() {
        return jdbcTemplate.query("SELECT history_id, user_id, company_id, company_code, username, login_time, logout_time, ip_address, user_agent, status_id, failure_reason FROM login_history ORDER BY history_id", this::mapRow);
    }
    public Optional<LoginHistory> findById(UUID id) {
        return jdbcTemplate.query("SELECT history_id, user_id, company_id, company_code, username, login_time, logout_time, ip_address, user_agent, status_id, failure_reason FROM login_history WHERE history_id = ?", this::mapRow, id).stream().findFirst();
    }
    public LoginHistory create(LoginHistoryUpsertRequest r) {
        String sql = "INSERT INTO login_history (user_id, company_id, company_code, username, login_time, logout_time, ip_address, user_agent, status_id, failure_reason) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING history_id, user_id, company_id, company_code, username, login_time, logout_time, ip_address, user_agent, status_id, failure_reason";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.company_id(), r.company_code(), r.username(), r.login_time(), r.logout_time(), r.ip_address(), r.user_agent(), r.status_id(), r.failure_reason());
    }
    public Optional<LoginHistory> update(UUID id, LoginHistoryUpsertRequest r) {
        String sql = """
                UPDATE login_history
                SET user_id = COALESCE(?, user_id),
                    company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    username = COALESCE(?, username),
                    login_time = COALESCE(?, login_time),
                    logout_time = COALESCE(?, logout_time),
                    ip_address = COALESCE(?, ip_address),
                    user_agent = COALESCE(?, user_agent),
                    status_id = COALESCE(?, status_id),
                    failure_reason = COALESCE(?, failure_reason)
                WHERE history_id = ?
                RETURNING history_id, user_id, company_id, company_code, username, login_time, logout_time, ip_address, user_agent, status_id, failure_reason
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.company_id(), r.company_code(), r.username(), r.login_time(), r.logout_time(), r.ip_address(), r.user_agent(), r.status_id(), r.failure_reason(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM login_history WHERE history_id = ?", id) > 0; }
    private LoginHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new LoginHistory(
                rs.getObject("history_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getString("username"),
                rs.getTimestamp("login_time") == null ? null : rs.getTimestamp("login_time").toInstant(),
                rs.getTimestamp("logout_time") == null ? null : rs.getTimestamp("logout_time").toInstant(),
                rs.getString("ip_address"),
                rs.getString("user_agent"),
                rs.getObject("status_id", java.lang.Integer.class),
                rs.getString("failure_reason")
        );
    }
}








