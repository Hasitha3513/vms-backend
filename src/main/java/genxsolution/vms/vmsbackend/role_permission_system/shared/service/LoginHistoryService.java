package genxsolution.vms.vmsbackend.role_permission_system.shared.service;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.LoginHistoryResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.LoginHistoryUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.shared.mapper.LoginHistoryMapper;
import genxsolution.vms.vmsbackend.role_permission_system.shared.repository.LoginHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoginHistoryService {
    private final LoginHistoryRepository repository;
    private final LoginHistoryMapper mapper;
    public LoginHistoryService(LoginHistoryRepository repository, LoginHistoryMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<LoginHistoryResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public LoginHistoryResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("LoginHistory", id.toString())); }
    public LoginHistoryResponse create(LoginHistoryUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public LoginHistoryResponse update(UUID id, LoginHistoryUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("LoginHistory", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("LoginHistory", id.toString()); } }
}








