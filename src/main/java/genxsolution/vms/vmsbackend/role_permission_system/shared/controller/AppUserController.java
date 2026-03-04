package genxsolution.vms.vmsbackend.role_permission_system.shared.controller;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserPasswordResetRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserPasswordVerifyRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AppUserPasswordVerifyResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.service.AppUserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/app-users")
public class AppUserController {
    private final AppUserService service;
    public AppUserController(AppUserService service) { this.service = service; }
    @GetMapping public List<AppUserResponse> list() { return service.list(); }
    @GetMapping("/{id}") public AppUserResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public AppUserResponse create(@RequestBody AppUserUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public AppUserResponse update(@PathVariable UUID id, @RequestBody AppUserUpsertRequest request) { return service.update(id, request); }
    @PostMapping("/{id}/password/verify")
    public AppUserPasswordVerifyResponse verifyPassword(@PathVariable UUID id, @RequestBody AppUserPasswordVerifyRequest request) {
        return new AppUserPasswordVerifyResponse(service.verifyPassword(id, request.password()));
    }
    @PostMapping("/{id}/password/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void adminResetPassword(@PathVariable UUID id, @RequestBody AppUserPasswordResetRequest request) {
        service.adminResetPassword(id, request.newPassword());
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}








