package genxsolution.vms.vmsbackend.role_permission_system.shared.repository;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.PasswordResetTokenUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.PasswordResetToken;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordResetTokenRepository {
    private final JdbcTemplate jdbcTemplate;
    public PasswordResetTokenRepository(JdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public List<PasswordResetToken> findAll() {
        return jdbcTemplate.query("SELECT token_id, user_id, token_hash, expires_at, used_at, is_used, created_at FROM password_reset_token ORDER BY token_id", this::mapRow);
    }
    public Optional<PasswordResetToken> findById(UUID id) {
        return jdbcTemplate.query("SELECT token_id, user_id, token_hash, expires_at, used_at, is_used, created_at FROM password_reset_token WHERE token_id = ?", this::mapRow, id).stream().findFirst();
    }
    public PasswordResetToken create(PasswordResetTokenUpsertRequest r) {
        String sql = "INSERT INTO password_reset_token (user_id, token_hash, expires_at, used_at, is_used) VALUES (?, ?, ?, ?, ?) RETURNING token_id, user_id, token_hash, expires_at, used_at, is_used, created_at";
        return jdbcTemplate.queryForObject(sql, this::mapRow, r.user_id(), r.token_hash(), r.expires_at(), r.used_at(), r.is_used());
    }
    public Optional<PasswordResetToken> update(UUID id, PasswordResetTokenUpsertRequest r) {
        String sql = """
                UPDATE password_reset_token
                SET user_id = COALESCE(?, user_id),
                    token_hash = COALESCE(?, token_hash),
                    expires_at = COALESCE(?, expires_at),
                    used_at = COALESCE(?, used_at),
                    is_used = COALESCE(?, is_used)
                WHERE token_id = ?
                RETURNING token_id, user_id, token_hash, expires_at, used_at, is_used, created_at
                """;
        return jdbcTemplate.query(sql, this::mapRow, r.user_id(), r.token_hash(), r.expires_at(), r.used_at(), r.is_used(), id).stream().findFirst();
    }
    public boolean delete(UUID id) { return jdbcTemplate.update("DELETE FROM password_reset_token WHERE token_id = ?", id) > 0; }
    private PasswordResetToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PasswordResetToken(
                rs.getObject("token_id", java.util.UUID.class),
                rs.getObject("user_id", java.util.UUID.class),
                rs.getString("token_hash"),
                rs.getTimestamp("expires_at") == null ? null : rs.getTimestamp("expires_at").toInstant(),
                rs.getTimestamp("used_at") == null ? null : rs.getTimestamp("used_at").toInstant(),
                rs.getObject("is_used", java.lang.Boolean.class),
                rs.getTimestamp("created_at") == null ? null : rs.getTimestamp("created_at").toInstant()
        );
    }
}








