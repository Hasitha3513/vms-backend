package genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.service;

import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.dto.UserDataScopeResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.dto.UserDataScopeUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.mapper.UserDataScopeMapper;
import genxsolution.vms.vmsbackend.role_permission_system.user_data_scopee.repository.UserDataScopeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserDataScopeService {
    private final UserDataScopeRepository repository;
    private final UserDataScopeMapper mapper;
    public UserDataScopeService(UserDataScopeRepository repository, UserDataScopeMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<UserDataScopeResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public UserDataScopeResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserDataScope", id.toString())); }
    public UserDataScopeResponse create(UserDataScopeUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public UserDataScopeResponse update(UUID id, UserDataScopeUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserDataScope", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("UserDataScope", id.toString()); } }
}









