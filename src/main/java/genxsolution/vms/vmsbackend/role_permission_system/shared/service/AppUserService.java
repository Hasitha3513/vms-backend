package genxsolution.vms.vmsbackend.role_permission_system.shared.service;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserUpsertRequest;
import genxsolution.vms.vmsbackend.authentication.AuthContext;
import genxsolution.vms.vmsbackend.authentication.AuthContextHolder;
import genxsolution.vms.vmsbackend.authentication.AccessGuard;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.shared.mapper.AppUserMapper;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.AppUser;
import genxsolution.vms.vmsbackend.role_permission_system.shared.repository.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AppUserService {
    private final AppUserRepository repository;
    private final AppUserMapper mapper;
    private final BCryptPasswordEncoder encoder;
    public AppUserService(AppUserRepository repository, AppUserMapper mapper, BCryptPasswordEncoder encoder) { this.repository = repository; this.mapper = mapper; this.encoder = encoder; }
    public List<AppUserResponse> list() {
        AuthContext ctx = AuthContextHolder.get();
        if (ctx != null && !ctx.superAdmin()) {
            return repository.findAllByCompanyCode(ctx.companyCode()).stream().map(mapper::toResponse).toList();
        }
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }
    public AppUserResponse getById(UUID id) {
        AccessGuard.requireAdmin();
        AppUser target = requireAccessibleUser(id);
        return mapper.toResponse(target);
    }
    public AppUserResponse create(AppUserUpsertRequest request) {
        AccessGuard.requireAdmin();
        AppUserUpsertRequest normalized = normalizeRequest(request);
        String username = normalized.username() == null ? null : normalized.username().toString().trim();
        String email = normalized.email() == null ? null : normalized.email().toString().trim();
        String passwordHash = normalized.password_hash() == null ? null : normalized.password_hash().toString().trim();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password is required for new user");
        }
        UUID employeeId = parseUuid(normalized.employee_id());
        if (employeeId != null && repository.existsByEmployeeId(employeeId)) {
            throw new IllegalArgumentException("Only one user account can be created per employee");
        }
        return mapper.toResponse(repository.create(normalized));
    }
    public AppUserResponse update(UUID id, AppUserUpsertRequest request) {
        AccessGuard.requireAdmin();
        requireAccessibleUser(id);
        AppUserUpsertRequest normalized = normalizeRequest(request);
        UUID employeeId = parseUuid(normalized.employee_id());
        if (employeeId != null && repository.existsByEmployeeIdAndUserIdNot(employeeId, id)) {
            throw new IllegalArgumentException("Only one user account can be created per employee");
        }
        return repository.update(id, normalized).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("AppUser", id.toString()));
    }
    public void delete(UUID id) {
        AccessGuard.requireAdmin();
        requireAccessibleUser(id);
        if (!repository.delete(id)) { throw new AccessResourceNotFoundException("AppUser", id.toString()); }
    }
    public boolean verifyPassword(UUID id, String plainPassword) {
        AccessGuard.requireAdmin();
        requireAccessibleUser(id);
        if (plainPassword == null || plainPassword.isBlank()) {
            return false;
        }
        String hash = repository.findPasswordHashById(id)
                .orElseThrow(() -> new AccessResourceNotFoundException("AppUser", id.toString()));
        return hash != null && !hash.isBlank() && encoder.matches(plainPassword, hash);
    }
    public void adminResetPassword(UUID id, String plainPassword) {
        AccessGuard.requireAdmin();
        requireAccessibleUser(id);
        if (plainPassword == null || plainPassword.isBlank()) {
            throw new IllegalArgumentException("New password is required");
        }
        String hash = encoder.encode(plainPassword);
        boolean updated = repository.updatePasswordHash(id, hash, Instant.now());
        if (!updated) {
            throw new AccessResourceNotFoundException("AppUser", id.toString());
        }
    }

    private AppUserUpsertRequest normalizeRequest(AppUserUpsertRequest request) {
        AuthContext ctx = AuthContextHolder.get();
        UUID companyId = parseUuid(request.company_id());
        UUID employeeId = parseUuid(request.employee_id());
        Boolean requestedSuperAdmin = toBooleanObject(request.is_super_admin());
        Boolean requestedCompanyAdmin = toBooleanObject(request.is_company_admin());
        boolean isSuperAdmin = Boolean.TRUE.equals(requestedSuperAdmin);
        boolean isCompanyAdmin = Boolean.TRUE.equals(requestedCompanyAdmin);
        String companyCode = request.company_code() == null ? null : request.company_code().toString().trim();
        if (companyCode != null && companyCode.isBlank()) {
            companyCode = null;
        }
        if (ctx != null && !ctx.superAdmin()) {
            if (isSuperAdmin) {
                throw new IllegalArgumentException("Company Super Admin cannot create or grant Super Admin access");
            }
            if (ctx.companyCode() == null || ctx.companyCode().isBlank()) {
                throw new IllegalArgumentException("Missing company scope for Company Super Admin");
            }
            if (companyCode == null || companyCode.isBlank()) {
                companyCode = ctx.companyCode();
            }
            if (!ctx.companyCode().equalsIgnoreCase(companyCode)) {
                throw new IllegalArgumentException("Company Super Admin can manage users only inside own company");
            }
        }
        String username = request.username() == null ? null : request.username().toString().trim();
        if (username != null && username.isBlank()) {
            username = null;
        }
        if (username != null && !isSuperAdmin) {
            int dotIndex = username.lastIndexOf('.');
            int atIndex = username.lastIndexOf('@');
            int sepIndex = Math.max(dotIndex, atIndex);
            if (sepIndex > 0 && sepIndex < username.length() - 1) {
                String suffix = username.substring(sepIndex + 1);
                String base = username.substring(0, sepIndex);
                if (companyCode != null && suffix.equalsIgnoreCase(companyCode)) {
                    username = base;
                }
            }
        }
        String passwordHash = request.password_hash() == null ? null : request.password_hash().toString();
        if (passwordHash != null) {
            passwordHash = passwordHash.trim();
            if (passwordHash.isBlank()) {
                passwordHash = null;
            }
        }
        if (passwordHash != null && !passwordHash.startsWith("$2a$") && !passwordHash.startsWith("$2b$")) {
            passwordHash = encoder.encode(passwordHash);
        }
        return new AppUserUpsertRequest(
                companyId,
                companyCode,
                employeeId,
                username,
                request.email(),
                passwordHash,
                requestedSuperAdmin,
                requestedCompanyAdmin,
                toBooleanObject(request.is_active()),
                toBooleanObject(request.is_locked()),
                toIntegerObject(request.failed_login_attempts()),
                null,
                null,
                toBooleanObject(request.email_verified()),
                null
        );
    }

    private AppUser requireAccessibleUser(UUID id) {
        AppUser target = repository.findById(id)
                .orElseThrow(() -> new AccessResourceNotFoundException("AppUser", id.toString()));
        AuthContext ctx = AuthContextHolder.get();
        if (ctx != null && !ctx.superAdmin()) {
            String actorCompany = ctx.companyCode() == null ? "" : ctx.companyCode().trim();
            String targetCompany = target.company_code() == null ? "" : target.company_code().trim();
            if (actorCompany.isBlank() || targetCompany.isBlank() || !actorCompany.equalsIgnoreCase(targetCompany)) {
                throw new SecurityException("Company Super Admin can manage users only inside own company");
            }
            if (Boolean.TRUE.equals(target.is_super_admin())) {
                throw new SecurityException("Company Super Admin cannot manage Super Admin accounts");
            }
        }
        return target;
    }

    private UUID parseUuid(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return value instanceof UUID u ? u : UUID.fromString(value.toString().trim());
        } catch (Exception ex) {
            return null;
        }
    }

    private Boolean toBooleanObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean b) {
            return b;
        }
        String s = value.toString().trim();
        if (s.isEmpty()) {
            return null;
        }
        return Boolean.parseBoolean(s);
    }

    private Integer toIntegerObject(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer i) {
            return i;
        }
        String s = value.toString().trim();
        if (s.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}








