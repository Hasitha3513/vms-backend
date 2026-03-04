package genxsolution.vms.vmsbackend.role_permission_system.shared.controller;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserHistoryResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.UserHistoryUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.service.UserHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/user-histories")
public class UserHistoryController {
    private final UserHistoryService service;
    public UserHistoryController(UserHistoryService service) { this.service = service; }
    @GetMapping public List<UserHistoryResponse> list() { return service.list(); }
    @GetMapping("/{id}") public UserHistoryResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UserHistoryResponse create(@RequestBody UserHistoryUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public UserHistoryResponse update(@PathVariable UUID id, @RequestBody UserHistoryUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}








