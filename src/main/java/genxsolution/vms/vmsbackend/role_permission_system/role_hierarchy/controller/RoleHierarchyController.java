package genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.controller;

import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.dto.RoleHierarchyResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.dto.RoleHierarchyUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.service.RoleHierarchyService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/role-hierarchies")
public class RoleHierarchyController {
    private final RoleHierarchyService service;
    public RoleHierarchyController(RoleHierarchyService service) { this.service = service; }
    @GetMapping public List<RoleHierarchyResponse> list() { return service.list(); }
    @GetMapping("/{id}") public RoleHierarchyResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RoleHierarchyResponse create(@RequestBody RoleHierarchyUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public RoleHierarchyResponse update(@PathVariable UUID id, @RequestBody RoleHierarchyUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}









