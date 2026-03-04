package genxsolution.vms.vmsbackend.role_permission_system.shared.controller;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.LoginHistoryResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.LoginHistoryUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.service.LoginHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/login-histories")
public class LoginHistoryController {
    private final LoginHistoryService service;
    public LoginHistoryController(LoginHistoryService service) { this.service = service; }
    @GetMapping public List<LoginHistoryResponse> list() { return service.list(); }
    @GetMapping("/{id}") public LoginHistoryResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public LoginHistoryResponse create(@RequestBody LoginHistoryUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public LoginHistoryResponse update(@PathVariable UUID id, @RequestBody LoginHistoryUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}








