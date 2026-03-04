package genxsolution.vms.vmsbackend.role_permission_system.shared.repository;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserSessionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.UserSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserSessionRepository {
    private final JdbcTemplate jdbcTemplate;
    public UserSessionRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<UserSession> findAll() {
        return jdbcTemplate.query("SELECT session_id, user_id, company_id, company_code, session_token, ip_address, user_agent, created_at, last_activity, expires_at, is_active FROM user_session ORDER BY session_id", this::mapRow);
    }
    public Optional<UserSession> findById(UUID id) {
        return jdbcTemplate.query("SELECT session_id, user_id, company_id, company_code, session_token, ip_address, user_agent, created_at, last_activity, expires_at, is_active FROM user_session WHERE session_id = ?", this::mapRow, id).stream().findFirst();
    }
    public UserSession create(UserSessionUpsertRequest r) {
        String sql = "INSERT INTO user_session (user_id, company_id, company_code, session_token, ip_address, user_agent, last_activity, expires_at, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING session_id, user_id, company_id, company_code, session_token, ip_address, user_agent, created_at, last_activity, expires_at, is_active";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.company_id(), r.company_code(), r.session_token(), r.ip_address(), r.user_agent(), r.last_activity(), r.expires_at(), r.is_active());
    }
    public Optional<UserSession> update(UUID id, UserSessionUpsertRequest r) {
        String sql = """
                UPDATE user_session
                SET user_id = COALESCE(?, user_id),
                    company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    session_token = COALESCE(?, session_token),
                    ip_address = COALESCE(?, ip_address),
                    user_agent = COALESCE(?, user_agent),
                    last_activity = COALESCE(?, last_activity),
                    expires_at = COALESCE(?, expires_at),
                    is_active = COALESCE(?, is_active)
                WHERE session_id = ?
                RETURNING session_id, user_id, company_id, company_code, session_token, ip_address, user_agent, created_at, last_activity, expires_at, is_active
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.company_id(), r.company_code(), r.session_token(), r.ip_address(), r.user_agent(), r.last_activity(), r.expires_at(), r.is_active(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM user_session WHERE session_id = ?", id) > 0; }
    private UserSession mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new UserSession(
                rs.getObject("session_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getString("session_token"),
                rs.getString("ip_address"),
                rs.getString("user_agent"),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("last_activity") == null ? null : rs.getTimestamp("last_activity").toInstant(),
                rs.getTimestamp("expires_at") == null ? null : rs.getTimestamp("expires_at").toInstant(),
                rs.getObject("is_active", java.lang.Boolean.class)
        );
    }
}








