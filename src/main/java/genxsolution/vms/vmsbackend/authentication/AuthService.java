package genxsolution.vms.vmsbackend.authentication;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.*;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.AppUser;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.UserSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.net.InetAddress;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final long SESSION_IDLE_TIMEOUT_MINUTES = 60;

    private final AuthRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final SuperAdminBootstrap superAdminBootstrap;
    private final String bootstrapUsername;
    private final String bootstrapEmail;
    private final String bootstrapPassword;

    public AuthService(
            AuthRepository repository,
            BCryptPasswordEncoder encoder,
            SuperAdminBootstrap superAdminBootstrap,
            @Value("${vms.auth.bootstrap-super-admin.username:superadmin}") String bootstrapUsername,
            @Value("${vms.auth.bootstrap-super-admin.email:superadmin@vms.local}") String bootstrapEmail,
            @Value("${vms.auth.bootstrap-super-admin.password:SuperAdmin@123}") String bootstrapPassword
    ) {
        this.repository = repository;
        this.encoder = encoder;
        this.superAdminBootstrap = superAdminBootstrap;
        this.bootstrapUsername = bootstrapUsername;
        this.bootstrapEmail = bootstrapEmail;
        this.bootstrapPassword = bootstrapPassword;
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Instant now = Instant.now();
            String safeIp = normalizeIpAddress(request.ipAddress());
            String principal = request.username() == null ? "" : request.username().trim();
            String normalizedPrincipal = normalizeCompanyScopedPrincipal(principal);

            // Guaranteed fallback for configured bootstrap super admin credentials.
            if (matchesBootstrapPrincipal(normalizedPrincipal) && bootstrapPassword.equals(request.password())) {
                superAdminBootstrap.ensureConfiguredSuperAdmin();
                AppUser bootstrapUser = repository.findUserByUsernameOrEmail(normalizedPrincipal)
                        .orElseGet(() -> repository.findUserByUsernameOrEmail(bootstrapEmail).orElseThrow(() -> new IllegalArgumentException("Super admin bootstrap failed")));
                return buildLoginResponse(bootstrapUser, now, safeIp, request.userAgent(), "LOGIN_SUCCESS_BOOTSTRAP");
            }

            if (superAdminBootstrap.matchesConfiguredPrincipal(normalizedPrincipal)) {
                superAdminBootstrap.ensureConfiguredSuperAdmin();
            }

            AppUser user = resolveLoginUser(normalizedPrincipal)
                    .orElseThrow(() -> invalidLoginIdentifier(normalizedPrincipal));

            if (Boolean.FALSE.equals(user.is_active()) || Boolean.TRUE.equals(user.is_locked())) {
                safeInsertLoginHistory(user.user_id(), user.company_id(), user.company_code(), user.username(), safeIp, request.userAgent(), "USER_DISABLED_OR_LOCKED");
                throw new IllegalArgumentException("User disabled or locked");
            }

            if (user.password_hash() == null || user.password_hash().isBlank()) {
                safeInsertLoginHistory(user.user_id(), user.company_id(), user.company_code(), user.username(), safeIp, request.userAgent(), "PASSWORD_NOT_SET");
                throw new IllegalArgumentException("Password not set for this user");
            }

            if (!passwordMatchesAndUpgrade(user, request.password())) {
                repository.registerLoginFailure(user.user_id());
                safeInsertLoginHistory(user.user_id(), user.company_id(), user.company_code(), user.username(), safeIp, request.userAgent(), "INVALID_PASSWORD");
                throw new IllegalArgumentException("Invalid credentials");
            }

            return buildLoginResponse(user, now, safeIp, request.userAgent(), "LOGIN_SUCCESS");
        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Unexpected login failure for user={}", request.username(), ex);
            throw new IllegalArgumentException("Login failed");
        }
    }

    public void logout(String sessionToken, String reason) {
        Instant now = Instant.now();
        repository.findActiveSessionByToken(sessionToken, now).ifPresent(session -> {
            repository.deactivateSessionByToken(sessionToken);
            repository.markLogout(session.user_id(), now, reason == null ? "LOGOUT" : reason);
        });
    }

    public AuthContext authenticate(String sessionToken) {
        Instant now = Instant.now();
        UserSession session = repository.findActiveSessionByToken(sessionToken, now)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired session"));
        AppUser user = repository.findByUserId(session.user_id())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (Boolean.FALSE.equals(user.is_active()) || Boolean.TRUE.equals(user.is_locked())) {
            throw new IllegalArgumentException("User disabled or locked");
        }
        Instant nextExpiresAt = now.plus(SESSION_IDLE_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        repository.touchSession(session.session_id(), now, nextExpiresAt);
        return new AuthContext(user.user_id(), user.username(), user.company_code(), Boolean.TRUE.equals(user.is_super_admin()), Boolean.TRUE.equals(user.is_company_admin()), session.session_id(), nextExpiresAt);
    }

    public void changeOwnPassword(AuthContext context, ChangePasswordRequest request) {
        AppUser user = repository.findByUserId(context.userId()).orElseThrow();
        if (!encoder.matches(request.currentPassword(), user.password_hash())) {
            throw new IllegalArgumentException("Current password invalid");
        }
        repository.changePassword(user.user_id(), encoder.encode(request.newPassword()), Instant.now());
    }

    public void adminResetPassword(AuthContext context, ResetPasswordRequest request) {
        if (!context.superAdmin() && !context.companyAdmin()) {
            throw new IllegalArgumentException("Admin only");
        }
        repository.changePassword(request.userId(), encoder.encode(request.newPassword()), Instant.now());
    }

    public List<ActiveSessionResponse> listActiveSessions(AuthContext context) {
        if (!context.superAdmin() && !context.companyAdmin()) {
            throw new IllegalArgumentException("Admin only");
        }
        return repository.listActiveSessions().stream()
                .map(s -> new ActiveSessionResponse(s.session_id(), s.user_id(), null, s.company_code(), s.ip_address(), s.user_agent(), s.created_at(), s.last_activity(), s.expires_at()))
                .toList();
    }

    public void revokeSession(AuthContext context, UUID sessionId) {
        if (!context.superAdmin() && !context.companyAdmin()) {
            throw new IllegalArgumentException("Admin only");
        }
        repository.deactivateSessionById(sessionId);
    }

    private String normalizeIpAddress(String rawIp) {
        if (rawIp == null || rawIp.isBlank()) {
            return null;
        }
        try {
            return InetAddress.getByName(rawIp).getHostAddress();
        } catch (Exception ex) {
            return null;
        }
    }

    private void safeInsertLoginHistory(UUID userId, UUID companyId, String companyCode, String username, String ip, String userAgent, String reason) {
        try {
            repository.insertLoginHistory(userId, companyId, companyCode, username, ip, userAgent, reason);
        } catch (Exception ignored) {
            // Do not fail authentication flow due to logging issues.
        }
    }

    private boolean matchesBootstrapPrincipal(String principal) {
        return principal != null && (principal.equalsIgnoreCase(bootstrapUsername) || principal.equalsIgnoreCase(bootstrapEmail));
    }

    private Optional<AppUser> resolveLoginUser(String principal) {
        if (principal == null || principal.isBlank()) {
            return Optional.empty();
        }
        int dotIndex = principal.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < principal.length() - 1 && !principal.contains("@")) {
            String username = principal.substring(0, dotIndex).trim();
            String companyCode = principal.substring(dotIndex + 1).trim();
            if (!username.isBlank() && !companyCode.isBlank()) {
                Optional<AppUser> byUsernameAndCompany = repository.findUserByUsernameAndCompanyCode(username, companyCode);
                if (byUsernameAndCompany.isPresent()) {
                    return byUsernameAndCompany;
                }
            }
        }
        Optional<AppUser> exact = repository.findUserByUsernameOrEmail(principal);
        if (exact.isPresent()) {
            return exact;
        }
        // Backward compatibility for older usernames stored as username@companycode.
        if (dotIndex > 0 && dotIndex < principal.length() - 1 && !principal.contains("@")) {
            String legacy = principal.substring(0, dotIndex) + "@" + principal.substring(dotIndex + 1);
            return repository.findUserByUsernameOrEmail(legacy);
        }
        return Optional.empty();
    }

    private IllegalArgumentException invalidLoginIdentifier(String principal) {
        return new IllegalArgumentException("Invalid credentials");
    }

    private String normalizeCompanyScopedPrincipal(String principal) {
        if (principal == null) {
            return "";
        }
        String value = principal.trim();
        if (value.isEmpty()) {
            return value;
        }
        if (value.contains("@")) {
            return value;
        }
        if (matchesBootstrapPrincipal(value)) {
            return value;
        }
        return value.toLowerCase();
    }

    private LoginResponse buildLoginResponse(AppUser user, Instant now, String ip, String userAgent, String loginReason) {
        repository.setLoginSuccess(user.user_id(), now);
        Instant nextExpiresAt = now.plus(SESSION_IDLE_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        UserSession session = repository.findActiveSessionByUser(user.user_id(), now)
                .map(s -> {
                    repository.touchSession(s.session_id(), now, nextExpiresAt);
                    return new UserSession(
                            s.session_id(),
                            s.user_id(),
                            s.company_id(),
                            s.company_code(),
                            s.session_token(),
                            s.ip_address(),
                            s.user_agent(),
                            s.created_at(),
                            now,
                            nextExpiresAt,
                            s.is_active()
                    );
                })
                .orElseGet(() -> {
                    return repository.createSession(user.user_id(), user.company_id(), user.company_code(), UUID.randomUUID().toString().replace("-", ""), ip, userAgent, now, nextExpiresAt);
                });

        safeInsertLoginHistory(user.user_id(), user.company_id(), user.company_code(), user.username(), ip, userAgent, loginReason);
        String employeeName = repository.findEmployeeDisplayNameByUserId(user.user_id()).orElse(null);
        return new LoginResponse(
                user.user_id(),
                user.username(),
                user.company_code(),
                employeeName,
                session.session_token(),
                session.expires_at(),
                Boolean.TRUE.equals(user.is_super_admin()),
                Boolean.TRUE.equals(user.is_company_admin())
        );
    }

    private boolean passwordMatchesAndUpgrade(AppUser user, String rawPassword) {
        String stored = user.password_hash();
        if (stored == null || stored.isBlank()) {
            return false;
        }
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return encoder.matches(rawPassword, stored);
        }
        // Legacy/plaintext fallback support: authenticate once and upgrade to bcrypt.
        if (stored.equals(rawPassword)) {
            repository.changePassword(user.user_id(), encoder.encode(rawPassword), Instant.now());
            return true;
        }
        return false;
    }
}
