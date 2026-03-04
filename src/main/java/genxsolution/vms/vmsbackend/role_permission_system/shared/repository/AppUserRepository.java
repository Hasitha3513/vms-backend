package genxsolution.vms.vmsbackend.role_permission_system.shared.repository;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.AppUser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AppUserRepository {
    private final JdbcTemplate jdbcTemplate;
    public AppUserRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<AppUser> findAll() {
        return jdbcTemplate.query("SELECT user_id, company_id, company_code, employee_id, username, email, password_hash, is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts, last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at FROM app_user ORDER BY user_id", this::mapRow);
    }
    public List<AppUser> findAllByCompanyCode(String companyCode) {
        return jdbcTemplate.query("SELECT user_id, company_id, company_code, employee_id, username, email, password_hash, is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts, last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at FROM app_user WHERE company_code = ? ORDER BY user_id", this::mapRow, companyCode);
    }
    public Optional<AppUser> findById(UUID id) {
        return jdbcTemplate.query("SELECT user_id, company_id, company_code, employee_id, username, email, password_hash, is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts, last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at FROM app_user WHERE user_id = ?", this::mapRow, id).stream().findFirst();
    }
    public boolean existsByEmployeeId(UUID employeeId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM app_user WHERE employee_id = ?",
                Integer.class,
                employeeId
        );
        return count != null && count > 0;
    }
    public boolean existsByEmployeeIdAndUserIdNot(UUID employeeId, UUID userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM app_user WHERE employee_id = ? AND user_id <> ?",
                Integer.class,
                employeeId,
                userId
        );
        return count != null && count > 0;
    }
    public Optional<String> findPasswordHashById(UUID id) {
        return jdbcTemplate.query("SELECT password_hash FROM app_user WHERE user_id = ?", (rs, n) -> rs.getString("password_hash"), id)
                .stream()
                .findFirst();
    }
    public AppUser create(AppUserUpsertRequest r) {
        String sql = "INSERT INTO app_user (company_id, company_code, employee_id, username, email, password_hash, is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts, last_login_at, password_changed_at, email_verified, email_verified_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING user_id, company_id, company_code, employee_id, username, email, password_hash, is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts, last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.company_id(), r.company_code(), r.employee_id(), r.username(), r.email(), r.password_hash(), r.is_super_admin(), r.is_company_admin(), r.is_active(), r.is_locked(), r.failed_login_attempts(), r.last_login_at(), r.password_changed_at(), r.email_verified(), r.email_verified_at());
    }
    public Optional<AppUser> update(UUID id, AppUserUpsertRequest r) {
        String sql = """
                UPDATE app_user
                SET company_id = COALESCE(?, company_id),
                    company_code = COALESCE(?, company_code),
                    employee_id = COALESCE(?, employee_id),
                    username = COALESCE(?, username),
                    email = COALESCE(?, email),
                    password_hash = COALESCE(?, password_hash),
                    is_super_admin = COALESCE(?, is_super_admin),
                    is_company_admin = COALESCE(?, is_company_admin),
                    is_active = COALESCE(?, is_active),
                    is_locked = COALESCE(?, is_locked),
                    failed_login_attempts = COALESCE(?, failed_login_attempts),
                    last_login_at = COALESCE(?, last_login_at),
                    password_changed_at = COALESCE(?, password_changed_at),
                    email_verified = COALESCE(?, email_verified),
                    email_verified_at = COALESCE(?, email_verified_at)
                WHERE user_id = ?
                RETURNING user_id, company_id, company_code, employee_id, username, email, password_hash, is_super_admin, is_company_admin, is_active, is_locked, failed_login_attempts, last_login_at, password_changed_at, email_verified, email_verified_at, created_at, updated_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.company_id(), r.company_code(), r.employee_id(), r.username(), r.email(), r.password_hash(), r.is_super_admin(), r.is_company_admin(), r.is_active(), r.is_locked(), r.failed_login_attempts(), r.last_login_at(), r.password_changed_at(), r.email_verified(), r.email_verified_at(), id).stream().findFirst();
    }
    public boolean updatePasswordHash(UUID id, String passwordHash, Instant changedAt) {
        return jdbcTemplate.update(
                "UPDATE app_user SET password_hash = ?, password_changed_at = ?, is_locked = FALSE, failed_login_attempts = 0 WHERE user_id = ?",
                passwordHash, Timestamp.from(changedAt), id
        ) > 0;
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM app_user WHERE user_id = ?", id) > 0; }
    private AppUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AppUser(
                rs.getObject("user_id", java.util.UUID.class),
                rs.getObject("company_id", java.util.UUID.class),
                rs.getString("company_code"),
                rs.getObject("employee_id", java.util.UUID.class),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password_hash"),
                rs.getObject("is_super_admin", java.lang.Boolean.class),
                rs.getObject("is_company_admin", java.lang.Boolean.class),
                rs.getObject("is_active", java.lang.Boolean.class),
                rs.getObject("is_locked", java.lang.Boolean.class),
                rs.getObject("failed_login_attempts", java.lang.Integer.class),
                rs.getTimestamp("last_login_at") == null ? null : rs.getTimestamp("last_login_at").toInstant(),
                rs.getTimestamp("password_changed_at") == null ? null : rs.getTimestamp("password_changed_at").toInstant(),
                rs.getObject("email_verified", java.lang.Boolean.class),
                rs.getTimestamp("email_verified_at") == null ? null : rs.getTimestamp("email_verified_at").toInstant(),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant(),
                rs.getTimestamp("updated_at") == null ? null : rs.getTimestamp("updated_at").toInstant()
        );
    }
}








