package genxsolution.vms.vmsbackend.dashboard.controller;

import genxsolution.vms.vmsbackend.dashboard.dto.DashboardConfigResponse;
import genxsolution.vms.vmsbackend.dashboard.dto.DashboardConfigUpsertRequest;
import genxsolution.vms.vmsbackend.dashboard.service.DashboardConfigService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/dashboard-configs")
public class DashboardConfigController {
    private final DashboardConfigService service;
    public DashboardConfigController(DashboardConfigService service) { this.service = service; }
    @GetMapping public List<DashboardConfigResponse> list() { return service.list(); }
    @GetMapping("/effective/me") public List<DashboardConfigResponse> effectiveMyDashboard() { return service.listForCurrentUser(); }
    @GetMapping("/effective/{userId}") public List<DashboardConfigResponse> effectiveByUser(@PathVariable UUID userId) { return service.listForUser(userId); }
    @GetMapping("/{id}") public DashboardConfigResponse getById(@PathVariable UUID id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public DashboardConfigResponse create(@RequestBody DashboardConfigUpsertRequest request) { return service.create(request); }
    @PutMapping("/{id}") public DashboardConfigResponse update(@PathVariable UUID id, @RequestBody DashboardConfigUpsertRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID id) { service.delete(id); }
}








