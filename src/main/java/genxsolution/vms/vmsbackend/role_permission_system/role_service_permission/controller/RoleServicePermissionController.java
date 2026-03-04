package genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.controller;

import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.dto.RoleServicePermissionResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.dto.RoleServicePermissionUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.role_service_permission.service.RoleServicePermissionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/role-service-permissions")
public class RoleServicePermissionController {
    private final RoleServicePermissionService service;
    public RoleServicePermissionController(RoleServicePermissionService service){ this.service=service; }
    @GetMapping public List<RoleServicePermissionResponse> list(){ return service.list(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RoleServicePermissionResponse upsert(@RequestBody RoleServicePermissionUpsertRequest request){ return service.upsert(request); }
    @DeleteMapping public void delete(@RequestParam UUID roleId, @RequestParam UUID serviceId){ service.delete(roleId, serviceId); }
}









