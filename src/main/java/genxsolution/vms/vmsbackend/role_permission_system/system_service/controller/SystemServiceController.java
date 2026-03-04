package genxsolution.vms.vmsbackend.role_permission_system.system_service.controller;

import genxsolution.vms.vmsbackend.role_permission_system.system_service.dto.SystemServiceResponse;
import genxsolution.vms.vmsbackend.role_permission_system.system_service.dto.SystemServiceUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.system_service.service.SystemServiceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/system-services")
public class SystemServiceController {
    private final SystemServiceService service;
    public SystemServiceController(SystemServiceService service) { this.service = service; }
    @GetMapping public List<SystemServiceResponse> list() { return service.list(); }
    @GetMapping("/{id}") public SystemServiceResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public SystemServiceResponse create(@RequestBody SystemServiceUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public SystemServiceResponse update(@PathVariable UUID id, @RequestBody SystemServiceUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}









