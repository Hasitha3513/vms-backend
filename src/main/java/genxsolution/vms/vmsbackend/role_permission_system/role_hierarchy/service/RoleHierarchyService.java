package genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.service;

import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.dto.RoleHierarchyResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.dto.RoleHierarchyUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.mapper.RoleHierarchyMapper;
import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.repository.RoleHierarchyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleHierarchyService {
    private final RoleHierarchyRepository repository;
    private final RoleHierarchyMapper mapper;
    public RoleHierarchyService(RoleHierarchyRepository repository, RoleHierarchyMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<RoleHierarchyResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public RoleHierarchyResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("RoleHierarchy", id.toString())); }
    public RoleHierarchyResponse create(RoleHierarchyUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public RoleHierarchyResponse update(UUID id, RoleHierarchyUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("RoleHierarchy", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("RoleHierarchy", id.toString()); } }
}









