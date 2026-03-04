package genxsolution.vms.vmsbackend.employee_hr_management.leave_application.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto.LeaveApplicationResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto.LeaveApplicationUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.service.LeaveApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr/leave-applications")
public class LeaveApplicationController {
    private final LeaveApplicationService service;
    public LeaveApplicationController(LeaveApplicationService service) { this.service = service; }
    @GetMapping public List<LeaveApplicationResponse> list(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) { return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId")); }
    @GetMapping("/{leaveId}") public LeaveApplicationResponse getById(@PathVariable UUID leaveId) { return service.getById(leaveId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public LeaveApplicationResponse create(@RequestBody LeaveApplicationUpsertRequest request) { return service.create(request); }
    @PutMapping("/{leaveId}") public LeaveApplicationResponse update(@PathVariable UUID leaveId, @RequestBody LeaveApplicationUpsertRequest request) { return service.update(leaveId, request); }
    @DeleteMapping("/{leaveId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID leaveId) { service.delete(leaveId); }
}
