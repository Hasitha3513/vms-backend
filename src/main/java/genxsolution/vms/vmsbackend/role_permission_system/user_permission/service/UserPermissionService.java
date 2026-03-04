package genxsolution.vms.vmsbackend.role_permission_system.user_permission.service;

import genxsolution.vms.vmsbackend.role_permission_system.user_permission.dto.UserPermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_permission.dto.UserPermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.user_permission.mapper.UserPermissionMapper;
import genxsolution.vms.vmsbackend.role_permission_system.user_permission.repository.UserPermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserPermissionService {
    private final UserPermissionRepository repository;
    private final UserPermissionMapper mapper;
    public UserPermissionService(UserPermissionRepository repository, UserPermissionMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<UserPermissionResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public UserPermissionResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserPermission", id.toString())); }
    public UserPermissionResponse create(UserPermissionUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public UserPermissionResponse update(UUID id, UserPermissionUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserPermission", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("UserPermission", id.toString()); } }
}









