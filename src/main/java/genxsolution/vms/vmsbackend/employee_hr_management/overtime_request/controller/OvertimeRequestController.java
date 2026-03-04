package genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.dto.OvertimeRequestResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.dto.OvertimeRequestUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.service.OvertimeRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr/overtime-requests")
public class OvertimeRequestController {
    private final OvertimeRequestService service;
    public OvertimeRequestController(OvertimeRequestService service) { this.service = service; }
    @GetMapping public List<OvertimeRequestResponse> list(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) { return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId")); }
    @GetMapping("/{overtimeId}") public OvertimeRequestResponse getById(@PathVariable UUID overtimeId) { return service.getById(overtimeId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public OvertimeRequestResponse create(@RequestBody OvertimeRequestUpsertRequest request) { return service.create(request); }
    @PutMapping("/{overtimeId}") public OvertimeRequestResponse update(@PathVariable UUID overtimeId, @RequestBody OvertimeRequestUpsertRequest request) { return service.update(overtimeId, request); }
    @DeleteMapping("/{overtimeId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID overtimeId) { service.delete(overtimeId); }
}
