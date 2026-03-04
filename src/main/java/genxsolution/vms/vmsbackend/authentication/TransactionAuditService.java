package genxsolution.vms.vmsbackend.authentication;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.UUID;

@Service
public class TransactionAuditService {

    private final JdbcTemplate jdbcTemplate;
    private final Path logPath = Path.of("audit", "transactions.jsonl");

    public TransactionAuditService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void log(String action, String path, int status, String ip, String userAgent) {
        try {
            Files.createDirectories(logPath.getParent());
            AuthContext ctx = AuthContextHolder.get();
            String json = String.format("{\"timestamp\":\"%s\",\"action\":\"%s\",\"path\":\"%s\",\"status\":%d,\"ip\":\"%s\",\"userAgent\":\"%s\",\"userId\":\"%s\",\"companyCode\":\"%s\"}%n",
                    Instant.now(), escape(action), escape(path), status, escape(ip), escape(userAgent),
                    ctx == null ? "" : ctx.userId(), ctx == null ? "" : ctx.companyCode());
            Files.writeString(logPath, json, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            if (ctx != null) {
                jdbcTemplate.update("""
                        INSERT INTO user_history (user_id, company_code, action, entity_type, new_values, ip_address, user_agent)
                        VALUES (?, ?, ?, ?, ?::jsonb, ?::inet, ?)
                        """,
                        ctx.userId(), ctx.companyCode(), action, path,
                        String.format("{\"status\":%d,\"path\":\"%s\"}", status, escape(path)),
                        ip == null || ip.isBlank() ? null : ip,
                        userAgent
                );
            }
        } catch (Exception ignored) {
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}






