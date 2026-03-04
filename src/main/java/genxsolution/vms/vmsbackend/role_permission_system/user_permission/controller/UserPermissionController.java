package genxsolution.vms.vmsbackend.role_permission_system.user_permission.controller;

import genxsolution.vms.vmsbackend.role_permission_system.user_permission.dto.UserPermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_permission.dto.UserPermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_permission.service.UserPermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/user-permissions")
public class UserPermissionController {
    private final UserPermissionService service;
    public UserPermissionController(UserPermissionService service) { this.service = service; }
    @GetMapping public List<UserPermissionResponse> list() { return service.list(); }
    @GetMapping("/{id}") public UserPermissionResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UserPermissionResponse create(@RequestBody UserPermissionUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public UserPermissionResponse update(@PathVariable UUID id, @RequestBody UserPermissionUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}









