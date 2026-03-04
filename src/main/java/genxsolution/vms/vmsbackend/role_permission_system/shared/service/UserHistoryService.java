package genxsolution.vms.vmsbackend.role_permission_system.shared.service;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserHistoryResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserHistoryUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.shared.mapper.UserHistoryMapper;
import genxsolution.vms.vmsbackend.role_permission_system.shared.repository.UserHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserHistoryService {
    private final UserHistoryRepository repository;
    private final UserHistoryMapper mapper;
    public UserHistoryService(UserHistoryRepository repository, UserHistoryMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<UserHistoryResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public UserHistoryResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserHistory", id.toString())); }
    public UserHistoryResponse create(UserHistoryUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public UserHistoryResponse update(UUID id, UserHistoryUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("UserHistory", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("UserHistory", id.toString()); } }
}








