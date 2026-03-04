package genxsolution.vms.vmsbackend.role_permission_system.shared.service;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.PasswordResetTokenResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.PasswordResetTokenUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.shared.mapper.PasswordResetTokenMapper;
import genxsolution.vms.vmsbackend.role_permission_system.shared.repository.PasswordResetTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PasswordResetTokenService {
    private final PasswordResetTokenRepository repository;
    private final PasswordResetTokenMapper mapper;
    public PasswordResetTokenService(PasswordResetTokenRepository repository, PasswordResetTokenMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<PasswordResetTokenResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public PasswordResetTokenResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("PasswordResetToken", id.toString())); }
    public PasswordResetTokenResponse create(PasswordResetTokenUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public PasswordResetTokenResponse update(UUID id, PasswordResetTokenUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("PasswordResetToken", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("PasswordResetToken", id.toString()); } }
}








