package genxsolution.vms.vmsbackend.employee_hr_management.leave_type.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.dto.LeaveTypeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.dto.LeaveTypeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.service.LeaveTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr/leave-types")
public class LeaveTypeController {
    private final LeaveTypeService service;
    public LeaveTypeController(LeaveTypeService service) { this.service = service; }
    @GetMapping public List<LeaveTypeResponse> list(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) { return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId")); }
    @GetMapping("/{leaveTypeId}") public LeaveTypeResponse getById(@PathVariable UUID leaveTypeId) { return service.getById(leaveTypeId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public LeaveTypeResponse create(@RequestBody LeaveTypeUpsertRequest request) { return service.create(request); }
    @PutMapping("/{leaveTypeId}") public LeaveTypeResponse update(@PathVariable UUID leaveTypeId, @RequestBody LeaveTypeUpsertRequest request) { return service.update(leaveTypeId, request); }
    @DeleteMapping("/{leaveTypeId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID leaveTypeId) { service.delete(leaveTypeId); }
}
