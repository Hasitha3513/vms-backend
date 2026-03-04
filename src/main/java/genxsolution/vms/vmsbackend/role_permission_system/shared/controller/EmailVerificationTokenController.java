package genxsolution.vms.vmsbackend.role_permission_system.shared.controller;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.EmailVerificationTokenResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.EmailVerificationTokenUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.service.EmailVerificationTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/email-verification-tokens")
public class EmailVerificationTokenController {
    private final EmailVerificationTokenService service;
    public EmailVerificationTokenController(EmailVerificationTokenService service) { this.service = service; }
    @GetMapping public List<EmailVerificationTokenResponse> list() { return service.list(); }
    @GetMapping("/{id}") public EmailVerificationTokenResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public EmailVerificationTokenResponse create(@RequestBody EmailVerificationTokenUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public EmailVerificationTokenResponse update(@PathVariable UUID id, @RequestBody EmailVerificationTokenUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}








