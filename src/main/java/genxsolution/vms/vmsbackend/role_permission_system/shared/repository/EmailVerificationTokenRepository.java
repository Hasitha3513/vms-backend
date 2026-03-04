package genxsolution.vms.vmsbackend.role_permission_system.shared.repository;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.EmailVerificationTokenUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.EmailVerificationToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EmailVerificationTokenRepository {
    private final JdbcTemplate jdbcTemplate;
    public EmailVerificationTokenRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<EmailVerificationToken> findAll() {
        return jdbcTemplate.query("SELECT token_id, user_id, token_hash, email, expires_at, verified_at, is_used, created_at FROM email_verification_token ORDER BY token_id", this::mapRow);
    }
    public Optional<EmailVerificationToken> findById(UUID id) {
        return jdbcTemplate.query("SELECT token_id, user_id, token_hash, email, expires_at, verified_at, is_used, created_at FROM email_verification_token WHERE token_id = ?", this::mapRow, id).stream().findFirst();
    }
    public EmailVerificationToken create(EmailVerificationTokenUpsertRequest r) {
        String sql = "INSERT INTO email_verification_token (user_id, token_hash, email, expires_at, verified_at, is_used) VALUES (?, ?, ?, ?, ?, ?) RETURNING token_id, user_id, token_hash, email, expires_at, verified_at, is_used, created_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.token_hash(), r.email(), r.expires_at(), r.verified_at(), r.is_used());
    }
    public Optional<EmailVerificationToken> update(UUID id, EmailVerificationTokenUpsertRequest r) {
        String sql = """
                UPDATE email_verification_token
                SET user_id = COALESCE(?, user_id),
                    token_hash = COALESCE(?, token_hash),
                    email = COALESCE(?, email),
                    expires_at = COALESCE(?, expires_at),
                    verified_at = COALESCE(?, verified_at),
                    is_used = COALESCE(?, is_used)
                WHERE token_id = ?
                RETURNING token_id, user_id, token_hash, email, expires_at, verified_at, is_used, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.token_hash(), r.email(), r.expires_at(), r.verified_at(), r.is_used(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM email_verification_token WHERE token_id = ?", id) > 0; }
    private EmailVerificationToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new EmailVerificationToken(
                rs.getObject("token_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getString("token_hash"),
                rs.getString("email"),
                rs.getTimestamp("expires_at") == null ? null : rs.getTimestamp("expires_at").toInstant(),
                rs.getTimestamp("verified_at") == null ? null : rs.getTimestamp("verified_at").toInstant(),
                rs.getObject("is_used", java.lang.Boolean.class),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant()
        );
    }
}








