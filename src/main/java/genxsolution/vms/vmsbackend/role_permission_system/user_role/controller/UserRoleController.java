package genxsolution.vms.vmsbackend.role_permission_system.user_role.controller;

import genxsolution.vms.vmsbackend.role_permission_system.user_role.dto.UserRoleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_role.dto.UserRoleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_role.service.UserRoleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/user-roles")
public class UserRoleController {
    private final UserRoleService service;
    public UserRoleController(UserRoleService service) { this.service = service; }
    @GetMapping public List<UserRoleResponse> list() { return service.list(); }
    @GetMapping("/{id}") public UserRoleResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UserRoleResponse create(@RequestBody UserRoleUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public UserRoleResponse update(@PathVariable UUID id, @RequestBody UserRoleUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}









