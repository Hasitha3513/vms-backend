package genxsolution.vms.vmsbackend.role_permission_system.shared.controller;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserSessionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserSessionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.service.UserSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/user-sessions")
public class UserSessionController {
    private final UserSessionService service;
    public UserSessionController(UserSessionService service) { this.service = service; }
    @GetMapping public List<UserSessionResponse> list() { return service.list(); }
    @GetMapping("/{id}") public UserSessionResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UserSessionResponse create(@RequestBody UserSessionUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public UserSessionResponse update(@PathVariable UUID id, @RequestBody UserSessionUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}








