package genxsolution.vms.vmsbackend.role_permission_system.role_permission.service;

import genxsolution.vms.vmsbackend.role_permission_system.role_permission.dto.RolePermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_permission.dto.RolePermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_permission.mapper.RolePermissionMapper;
import genxsolution.vms.vmsbackend.role_permission_system.role_permission.repository.RolePermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RolePermissionService {
    private final RolePermissionRepository repository;
    private final RolePermissionMapper mapper;
    public RolePermissionService(RolePermissionRepository repository, RolePermissionMapper mapper){ this.repository=repository; this.mapper=mapper; }
    public List<RolePermissionResponse> list(){ return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public RolePermissionResponse upsert(RolePermissionUpsertRequest request){ genxsolution.vms.vmsbackend.authentication.AccessGuard.requireAdmin(); return mapper.toResponse(repository.upsert(request)); }
    public void delete(UUID roleId, UUID permissionId){ genxsolution.vms.vmsbackend.authentication.AccessGuard.requireAdmin(); repository.delete(roleId, permissionId); }
}










