package genxsolution.vms.vmsbackend.role_permission_system.shared.service;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserSessionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserSessionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.shared.mapper.UserSessionMapper;
import genxsolution.vms.vmsbackend.role_permission_system.shared.repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserSessionService {
    private final UserSessionRepository repository;
    private final UserSessionMapper mapper;
    public UserSessionService(UserSessionRepository repository, UserSessionMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<UserSessionResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public UserSessionResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserSession", id.toString())); }
    public UserSessionResponse create(UserSessionUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public UserSessionResponse update(UUID id, UserSessionUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserSession", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("UserSession", id.toString()); } }
}








