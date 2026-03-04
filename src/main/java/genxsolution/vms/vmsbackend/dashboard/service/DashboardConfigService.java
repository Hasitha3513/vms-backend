package genxsolution.vms.vmsbackend.dashboard.service;

import genxsolution.vms.vmsbackend.authentication.AccessGuard;
import genxsolution.vms.vmsbackend.authentication.AuthContext;
import genxsolution.vms.vmsbackend.authentication.AuthContextHolder;
import genxsolution.vms.vmsbackend.dashboard.dto.DashboardConfigResponse;
import genxsolution.vms.vmsbackend.dashboard.dto.DashboardConfigUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.dashboard.mapper.DashboardConfigMapper;
import genxsolution.vms.vmsbackend.role_permission_system.shared.model.AppUser;
import genxsolution.vms.vmsbackend.dashboard.model.DashboardConfig;
import genxsolution.vms.vmsbackend.role_permission_system.shared.repository.AppUserRepository;
import genxsolution.vms.vmsbackend.dashboard.repository.DashboardConfigRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DashboardConfigService {
    private final DashboardConfigRepository repository;
    private final AppUserRepository appUserRepository;
    private final DashboardConfigMapper mapper;
    public DashboardConfigService(DashboardConfigRepository repository, AppUserRepository appUserRepository, DashboardConfigMapper mapper) {
        this.repository = repository;
        this.appUserRepository = appUserRepository;
        this.mapper = mapper;
    }
    public List<DashboardConfigResponse> list() {
        AccessGuard.requireAdmin();
        AuthContext context = requireContext();
        return repository.findAll().stream()
                .filter(item -> context.superAdmin() || (item.company_code() != null && context.companyCode().equalsIgnoreCase(item.company_code())))
                .map(mapper::toResponse)
                .toList();
    }
    public DashboardConfigResponse getById(UUID id) {
        AccessGuard.requireAdmin();
        DashboardConfig config = repository.findById(id).orElseThrow(() -> new AccessResourceNotFoundException("DashboardConfig", id.toString()));
        ensureAdminScopeForExisting(config);
        return mapper.toResponse(config);
    }

    public List<DashboardConfigResponse> listForCurrentUser() {
        AuthContext context = requireContext();
        return effectiveDashboard(context.userId(), context, false);
    }

    public List<DashboardConfigResponse> listForUser(UUID userId) {
        AuthContext context = requireContext();
        AccessGuard.requireAdmin();
        return effectiveDashboard(userId, context, true);
    }

    public DashboardConfigResponse create(DashboardConfigUpsertRequest request) {
        AccessGuard.requireAdmin();
        ensureAdminScopeForRequest(request);
        return mapper.toResponse(repository.create(request));
    }

    public DashboardConfigResponse update(UUID id, DashboardConfigUpsertRequest request) {
        AccessGuard.requireAdmin();
        DashboardConfig existing = repository.findById(id).orElseThrow(() -> new AccessResourceNotFoundException("DashboardConfig", id.toString()));
        ensureAdminScopeForExisting(existing);
        ensureAdminScopeForRequest(request);
        return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("DashboardConfig", id.toString()));
    }

    public void delete(UUID id) {
        AccessGuard.requireAdmin();
        DashboardConfig existing = repository.findById(id).orElseThrow(() -> new AccessResourceNotFoundException("DashboardConfig", id.toString()));
        ensureAdminScopeForExisting(existing);
        if (!repository.delete(id)) {
            throw new AccessResourceNotFoundException("DashboardConfig", id.toString());
        }
    }

    private List<DashboardConfigResponse> effectiveDashboard(UUID targetUserId, AuthContext context, boolean includeGlobalForSuperAdmin) {
        AppUser target = appUserRepository.findById(targetUserId)
                .orElseThrow(() -> new AccessResourceNotFoundException("AppUser", targetUserId.toString()));

        if (!context.superAdmin() && !context.companyCode().equalsIgnoreCase(target.company_code())) {
            throw new SecurityException("Company admin cannot view other company dashboard");
        }

        List<DashboardConfig> raw = repository.findEffectiveByUser(
                targetUserId,
                target.company_code(),
                includeGlobalForSuperAdmin && context.superAdmin()
        );

        Map<String, DashboardConfig> resolved = new LinkedHashMap<>();
        for (DashboardConfig item : raw) {
            String key = item.widget_name() == null ? item.config_id().toString() : item.widget_name();
            resolved.putIfAbsent(key, item);
        }

        return resolved.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.is_visible()))
                .map(mapper::toResponse)
                .toList();
    }

    private void ensureAdminScopeForExisting(DashboardConfig config) {
        AuthContext context = requireContext();
        if (!context.superAdmin()) {
            if (config.company_code() == null || !context.companyCode().equalsIgnoreCase(config.company_code())) {
                throw new SecurityException("Company admin cannot modify other company dashboard config");
            }
        }
    }

    private void ensureAdminScopeForRequest(DashboardConfigUpsertRequest request) {
        AuthContext context = requireContext();
        if (context.superAdmin()) {
            return;
        }
        String requestCompanyCode = valueAsString(request.company_code());
        if (requestCompanyCode == null || !context.companyCode().equalsIgnoreCase(requestCompanyCode)) {
            throw new SecurityException("Company admin can assign dashboard only inside own company");
        }
    }

    private String valueAsString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private AuthContext requireContext() {
        AuthContext context = AuthContextHolder.get();
        if (context == null) {
            throw new SecurityException("Unauthorized");
        }
        return context;
    }
}








