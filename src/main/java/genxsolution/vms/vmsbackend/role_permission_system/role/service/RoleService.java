package genxsolution.vms.vmsbackend.role_permission_system.role.service;

import genxsolution.vms.vmsbackend.role_permission_system.role.dto.RoleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role.dto.RoleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.role.mapper.RoleMapper;
import genxsolution.vms.vmsbackend.role_permission_system.role.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleService {
    private final RoleRepository repository;
    private final RoleMapper mapper;
    public RoleService(RoleRepository repository, RoleMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<RoleResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public RoleResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("Role", id.toString())); }
    public RoleResponse create(RoleUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public RoleResponse update(UUID id, RoleUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("Role", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("Role", id.toString()); } }
}









