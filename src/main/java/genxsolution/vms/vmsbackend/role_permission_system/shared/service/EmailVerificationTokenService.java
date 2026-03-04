package genxsolution.vms.vmsbackend.role_permission_system.shared.service;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.EmailVerificationTokenResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.EmailVerificationTokenUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.shared.mapper.EmailVerificationTokenMapper;
import genxsolution.vms.vmsbackend.role_permission_system.shared.repository.EmailVerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmailVerificationTokenService {
    private final EmailVerificationTokenRepository repository;
    private final EmailVerificationTokenMapper mapper;
    public EmailVerificationTokenService(EmailVerificationTokenRepository repository, EmailVerificationTokenMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<EmailVerificationTokenResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public EmailVerificationTokenResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("EmailVerificationToken", id.toString())); }
    public EmailVerificationTokenResponse create(EmailVerificationTokenUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public EmailVerificationTokenResponse update(UUID id, EmailVerificationTokenUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("EmailVerificationToken", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("EmailVerificationToken", id.toString()); } }
}








