package genxsolution.vms.vmsbackend.authentication;

import genxsolution.vms.vmsbackend.role_permission_system.shared.model.AppUser;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.UserSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AuthRepository {

    private final JdbcTemplate jdbcTemplate;

    public AuthRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<AppUser> findUserByUsernameOrEmail(String usernameOrEmail) {
        String sql = """
                SELECT user_id, company_id, company_code, employee_id, username, email, password_hash,
                       is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts,
                       last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at
                FROM app_user
                WHERE LOWER(username) = LOWER(?) OR LOWER(email) = LOWER(?)
                """;
        return jdbcTemplate.query(sql, (rs, n) -> new AppUser(
                rs.getObject("user_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("employee_id", UUID.class),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getObject("is_super_admin", Boolean.class),
                rs.getObject("is_company_admin", Boolean.class),
                rs.getObject("is_active", Boolean.class),
                rs.getObject("is_locked", Boolean.class),
                rs.getObject("failed_login_attempts", Integer.class),
                toInstant(rs, "last_login_at"),
                toInstant(rs, "password_changed_at"),
                rs.getObject("email_verified", Boolean.class),
                toInstant(rs, "email_verified_at"),
                toInstant(rs, "created_at"),
                toInstant(rs, "updated_at")
        ), usernameOrEmail, usernameOrEmail).stream().findFirst();
    }

    public Optional<AppUser> findUserByUsernameAndCompanyCode(String username, String companyCode) {
        String sql = """
                SELECT user_id, company_id, company_code, employee_id, username, email, password_hash,
                       is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts,
                       last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at
                FROM app_user
                WHERE LOWER(username) = LOWER(?) AND LOWER(company_code) = LOWER(?)
                """;
        return jdbcTemplate.query(sql, (rs, n) -> new AppUser(
                rs.getObject("user_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("employee_id", UUID.class),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getObject("is_super_admin", Boolean.class),
                rs.getObject("is_company_admin", Boolean.class),
                rs.getObject("is_active", Boolean.class),
                rs.getObject("is_locked", Boolean.class),
                rs.getObject("failed_login_attempts", Integer.class),
                toInstant(rs, "last_login_at"),
                toInstant(rs, "password_changed_at"),
                rs.getObject("email_verified", Boolean.class),
                toInstant(rs, "email_verified_at"),
                toInstant(rs, "created_at"),
                toInstant(rs, "updated_at")
        ), username, companyCode).stream().findFirst();
    }

    public Optional<AppUser> findByUserId(UUID userId) {
        String sql = """
                SELECT user_id, company_id, company_code, employee_id, username, email, password_hash,
                       is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts,
                       last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at
                FROM app_user
                WHERE user_id = ?
                """;
        return jdbcTemplate.query(sql, (rs, n) -> new AppUser(
                rs.getObject("user_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getObject("employee_id", UUID.class),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getObject("is_super_admin", Boolean.class),
                rs.getObject("is_company_admin", Boolean.class),
                rs.getObject("is_active", Boolean.class),
                rs.getObject("is_locked", Boolean.class),
                rs.getObject("failed_login_attempts", Integer.class),
                toInstant(rs, "last_login_at"),
                toInstant(rs, "password_changed_at"),
                rs.getObject("email_verified", Boolean.class),
                toInstant(rs, "email_verified_at"),
                toInstant(rs, "created_at"),
                toInstant(rs, "updated_at")
        ), userId).stream().findFirst();
    }

    public Optional<UserSession> findActiveSessionByUser(UUID userId, Instant now) {
        String sql = """
                SELECT session_id, user_id, company_id, company_code, session_token, ip_address,
                       user_agent, created_at, last_activity, expires_at, is_active
                FROM user_session
                WHERE user_id = ? AND is_active = TRUE AND expires_at > ?
                ORDER BY last_activity DESC
                LIMIT 1
                """;
        return jdbcTemplate.query(sql, this::mapUserSession, userId, java.sql.Timestamp.from(now)).stream().findFirst();
    }

    public Optional<UserSession> findActiveSessionByToken(String token, Instant now) {
        String sql = """
                SELECT session_id, user_id, company_id, company_code, session_token, ip_address,
                       user_agent, created_at, last_activity, expires_at, is_active
                FROM user_session
                WHERE session_token = ? AND is_active = TRUE AND expires_at > ?
                """;
        return jdbcTemplate.query(sql, this::mapUserSession, token, java.sql.Timestamp.from(now)).stream().findFirst();
    }

    public UserSession createSession(UUID userId, UUID companyId, String companyCode, String token, String ip, String agent, Instant now, Instant expiresAt) {
        String sql = """
                INSERT INTO user_session (user_id, company_id, company_code, session_token, ip_address, user_agent, created_at, last_activity, expires_at, is_active)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, TRUE)
                RETURNING session_id, user_id, company_id, company_code, session_token, ip_address,
                          user_agent, created_at, last_activity, expires_at, is_active
                """;
        return jdbcTemplate.queryForObject(sql, this::mapUserSession, userId, companyId, companyCode, token, ip, agent, java.sql.Timestamp.from(now), java.sql.Timestamp.from(now), java.sql.Timestamp.from(expiresAt));
    }

    public void touchSession(UUID sessionId, Instant now, Instant expiresAt) {
        jdbcTemplate.update(
                "UPDATE user_session SET last_activity = ?, expires_at = ? WHERE session_id = ?",
                java.sql.Timestamp.from(now),
                java.sql.Timestamp.from(expiresAt),
                sessionId
        );
    }

    public void deactivateSessionByToken(String token) {
        jdbcTemplate.update("UPDATE user_session SET is_active = FALSE WHERE session_token = ?", token);
    }

    public void deactivateSessionById(UUID sessionId) {
        jdbcTemplate.update("UPDATE user_session SET is_active = FALSE WHERE session_id = ?", sessionId);
    }

    public List<UserSession> listActiveSessions() {
        String sql = """
                SELECT session_id, user_id, company_id, company_code, session_token, ip_address,
                       user_agent, created_at, last_activity, expires_at, is_active
                FROM user_session WHERE is_active = TRUE AND expires_at > CURRENT_TIMESTAMP
                ORDER BY last_activity DESC
                """;
        return jdbcTemplate.query(sql, this::mapUserSession);
    }

    public void setLoginSuccess(UUID userId, Instant now) {
        jdbcTemplate.update("UPDATE app_user SET failed_login_attempts = 0, is_locked = FALSE, last_login_at = ? WHERE user_id = ?", java.sql.Timestamp.from(now), userId);
    }

    public void registerLoginFailure(UUID userId) {
        jdbcTemplate.update("UPDATE app_user SET failed_login_attempts = failed_login_attempts + 1, is_locked = CASE WHEN failed_login_attempts + 1 >= 5 THEN TRUE ELSE is_locked END WHERE user_id = ?", userId);
    }

    public void changePassword(UUID userId, String hash, Instant now) {
        jdbcTemplate.update("UPDATE app_user SET password_hash = ?, password_changed_at = ? WHERE user_id = ?", hash, java.sql.Timestamp.from(now), userId);
    }

    public void insertLoginHistory(UUID userId, UUID companyId, String companyCode, String username, String ip, String agent, String reason) {
        jdbcTemplate.update("INSERT INTO login_history (user_id, company_id, company_code, username, ip_address, user_agent, failure_reason) VALUES (?, ?, ?, ?, ?::inet, ?, ?)",
                userId, companyId, companyCode, username, ip, agent, reason);
    }

    public void markLogout(UUID userId, Instant now, String reason) {
        jdbcTemplate.update("UPDATE login_history SET logout_time = ?, failure_reason = COALESCE(failure_reason, ?) WHERE user_id = ? AND logout_time IS NULL", java.sql.Timestamp.from(now), reason, userId);
    }

    public Optional<String> findEmployeeDisplayNameByUserId(UUID userId) {
        String sql = """
                SELECT NULLIF(TRIM(CONCAT(COALESCE(e.first_name, ''), ' ', COALESCE(e.last_name, ''))), '') AS employee_name
                FROM app_user au
                LEFT JOIN employee e ON e.employee_id = au.employee_id
                WHERE au.user_id = ?
                """;
        return jdbcTemplate.query(sql, rs -> rs.next() ? Optional.ofNullable(rs.getString("employee_name")) : Optional.empty(), userId);
    }

    private UserSession mapUserSession(java.sql.ResultSet rs, int n) throws java.sql.SQLException {
        Instant now = Instant.now();
        java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
        java.sql.Timestamp activityTs = rs.getTimestamp("last_activity");
        java.sql.Timestamp expiresTs = rs.getTimestamp("expires_at");

        return new UserSession(
                rs.getObject("session_id", UUID.class),
                rs.getObject("user_id", UUID.class),
                rs.getObject("company_id", UUID.class),
                rs.getString("company_code"),
                rs.getString("session_token"),
                rs.getString("ip_address"),
                rs.getString("user_agent"),
                createdTs == null ? now : createdTs.toInstant(),
                activityTs == null ? now : activityTs.toInstant(),
                expiresTs == null ? now.plusSeconds(3600) : expiresTs.toInstant(),
                rs.getObject("is_active", Boolean.class)
        );
    }

    private Instant toInstant(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        java.sql.Timestamp ts = rs.getTimestamp(column);
        return ts == null ? null : ts.toInstant();
    }
}






