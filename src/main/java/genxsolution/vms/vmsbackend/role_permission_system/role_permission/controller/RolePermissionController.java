package genxsolution.vms.vmsbackend.role_permission_system.role_permission.controller;

import genxsolution.vms.vmsbackend.role_permission_system.role_permission.dto.RolePermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_permission.dto.RolePermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_permission.service.RolePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/role-permissions")
public class RolePermissionController {
    private final RolePermissionService service;
    public RolePermissionController(RolePermissionService service){ this.service=service; }
    @GetMapping public List<RolePermissionResponse> list(){ return service.list(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RolePermissionResponse upsert(@RequestBody RolePermissionUpsertRequest request){ return service.upsert(request); }
    @DeleteMapping public void delete(@RequestParam UUID roleId, @RequestParam UUID permissionId){ service.delete(roleId, permissionId); }
}









