package genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.service;

import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.dto.RoleServicePermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.dto.RoleServicePermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.mapper.RoleServicePermissionMapper;
import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.repository.RoleServicePermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServicePermissionService {
    private final RoleServicePermissionRepository repository;
    private final RoleServicePermissionMapper mapper;
    public RoleServicePermissionService(RoleServicePermissionRepository repository, RoleServicePermissionMapper mapper){ this.repository=repository; this.mapper=mapper; }
    public List<RoleServicePermissionResponse> list(){ return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public RoleServicePermissionResponse upsert(RoleServicePermissionUpsertRequest request){ genxsolution.vms.vmsbackend.authentication.AccessGuard.requireAdmin(); return mapper.toResponse(repository.upsert(request)); }
    public void delete(UUID roleId, UUID serviceId){ genxsolution.vms.vmsbackend.authentication.AccessGuard.requireAdmin(); repository.delete(roleId, serviceId); }
}










