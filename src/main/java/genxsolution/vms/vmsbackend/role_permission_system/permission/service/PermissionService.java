package genxsolution.vms.vmsbackend.role_permission_system.permission.service;

import genxsolution.vms.vmsbackend.role_permission_system.permission.dto.PermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.permission.dto.PermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.permission.mapper.PermissionMapper;
import genxsolution.vms.vmsbackend.role_permission_system.permission.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PermissionService {
    private final PermissionRepository repository;
    private final PermissionMapper mapper;
    public PermissionService(PermissionRepository repository, PermissionMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<PermissionResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public PermissionResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("Permission", id.toString())); }
    public PermissionResponse create(PermissionUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public PermissionResponse update(UUID id, PermissionUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("Permission", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("Permission", id.toString()); } }
}









