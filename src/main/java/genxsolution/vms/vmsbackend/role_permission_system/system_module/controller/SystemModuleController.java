package genxsolution.vms.vmsbackend.role_permission_system.system_module.controller;

import genxsolution.vms.vmsbackend.role_permission_system.system_module.dto.SystemModuleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.system_module.dto.SystemModuleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.system_module.service.SystemModuleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/system-modules")
public class SystemModuleController {
    private final SystemModuleService service;
    public SystemModuleController(SystemModuleService service) { this.service = service; }
    @GetMapping public List<SystemModuleResponse> list() { return service.list(); }
    @GetMapping("/{id}") public SystemModuleResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public SystemModuleResponse create(@RequestBody SystemModuleUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public SystemModuleResponse update(@PathVariable UUID id, @RequestBody SystemModuleUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}









