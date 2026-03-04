package genxsolution.vms.vmsbackend.role_permission_system.shared.controller;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.PasswordResetTokenResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.PasswordResetTokenUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.service.PasswordResetTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/password-reset-tokens")
public class PasswordResetTokenController {
    private final PasswordResetTokenService service;
    public PasswordResetTokenController(PasswordResetTokenService service) { this.service = service; }
    @GetMapping public List<PasswordResetTokenResponse> list() { return service.list(); }
    @GetMapping("/{id}") public PasswordResetTokenResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public PasswordResetTokenResponse create(@RequestBody PasswordResetTokenUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public PasswordResetTokenResponse update(@PathVariable UUID id, @RequestBody PasswordResetTokenUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}








