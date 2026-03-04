package genxsolution.vms.vmsbackend.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class SuperAdminBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SuperAdminBootstrap.class);

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder encoder;
    private final Environment environment;

    @Value("${vms.auth.bootstrap-super-admin.enabled:true}")
    private boolean enabled;
    @Value("${vms.auth.bootstrap-super-admin.dev-only:true}")
    private boolean devOnly;

    @Value("${vms.auth.bootstrap-super-admin.username:superadmin}")
    private String username;

    @Value("${vms.auth.bootstrap-super-admin.email:superadmin@vms.local}")
    private String email;

    @Value("${vms.auth.bootstrap-super-admin.password:SuperAdmin@123}")
    private String password;

    public SuperAdminBootstrap(JdbcTemplate jdbcTemplate, BCryptPasswordEncoder encoder, Environment environment) {
        this.jdbcTemplate = jdbcTemplate;
        this.encoder = encoder;
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        ensureConfiguredSuperAdmin();
    }

    public boolean matchesConfiguredPrincipal(String principal) {
        if (principal == null) {
            return false;
        }
        String value = principal.trim();
        return value.equalsIgnoreCase(username) || value.equalsIgnoreCase(email);
    }

    public void ensureConfiguredSuperAdmin() {
        if (!enabled) {
            return;
        }
        if (devOnly && !environment.acceptsProfiles(Profiles.of("dev", "local", "test"))) {
            return;
        }

        Optional<UUID> existingUser = jdbcTemplate.query(
                "SELECT user_id FROM app_user WHERE username = ? OR email = ? LIMIT 1",
                (rs, n) -> rs.getObject("user_id", UUID.class),
                username, email
        ).stream().findFirst();

        String passwordHash = encoder.encode(password);
        if (existingUser.isPresent()) {
            jdbcTemplate.update("""
                            UPDATE app_user
                            SET is_super_admin = TRUE,
                                is_company_admin = FALSE,
                                username = COALESCE(?, username),
                                email = COALESCE(?, email),
                                is_active = TRUE,
                                is_locked = FALSE,
                                failed_login_attempts = 0,
                                password_hash = ?,
                                password_changed_at = CURRENT_TIMESTAMP,
                                email_verified = TRUE,
                                email_verified_at = CURRENT_TIMESTAMP
                            WHERE user_id = ?
                            """,
                    username, email, passwordHash, existingUser.get()
            );
            log.warn("Upserted SUPER ADMIN account (existing user updated). username={}", username);
            return;
        }

        jdbcTemplate.update("""
                        INSERT INTO app_user (
                            username, email, password_hash, is_super_admin, is_company_admin,
                            is_active, is_locked, failed_login_attempts, email_verified, email_verified_at
                        )
                        VALUES (?, ?, ?, TRUE, FALSE, TRUE, FALSE, 0, TRUE, CURRENT_TIMESTAMP)
                        """,
                username, email, passwordHash
        );

        log.warn("Auto-created SUPER ADMIN account. username={}", username);
    }
}






