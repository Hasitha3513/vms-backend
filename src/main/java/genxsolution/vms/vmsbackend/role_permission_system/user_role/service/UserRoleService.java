package genxsolution.vms.vmsbackend.role_permission_system.user_role.service;

import genxsolution.vms.vmsbackend.role_permission_system.user_role.dto.UserRoleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_role.dto.UserRoleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.user_role.mapper.UserRoleMapper;
import genxsolution.vms.vmsbackend.role_permission_system.user_role.repository.UserRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserRoleService {
    private final UserRoleRepository repository;
    private final UserRoleMapper mapper;
    public UserRoleService(UserRoleRepository repository, UserRoleMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<UserRoleResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public UserRoleResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserRole", id.toString())); }
    public UserRoleResponse create(UserRoleUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public UserRoleResponse update(UUID id, UserRoleUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserRole", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("UserRole", id.toString()); } }
}









