package genxsolution.vms.vmsbackend.role_permission_system.role.controller;

import genxsolution.vms.vmsbackend.role_permission_system.role.dto.RoleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role.dto.RoleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/roles")
public class RoleController {
    private final RoleService service;
    public RoleController(RoleService service) { this.service = service; }
    @GetMapping public List<RoleResponse> list() { return service.list(); }
    @GetMapping("/{id}") public RoleResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RoleResponse create(@RequestBody RoleUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public RoleResponse update(@PathVariable UUID id, @RequestBody RoleUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}









