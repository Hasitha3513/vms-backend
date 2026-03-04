package genxsolution.vms.vmsbackend.employee_hr_management.attendance.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.dto.AttendanceResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.dto.AttendanceUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.service.AttendanceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr/attendances")
public class AttendanceController {
    private final AttendanceService service;
    public AttendanceController(AttendanceService service) { this.service = service; }
    @GetMapping public List<AttendanceResponse> list(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) { return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId")); }
    @GetMapping("/{attendanceId}") public AttendanceResponse getById(@PathVariable UUID attendanceId) { return service.getById(attendanceId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public AttendanceResponse create(@RequestBody AttendanceUpsertRequest request) { return service.create(request); }
    @PutMapping("/{attendanceId}") public AttendanceResponse update(@PathVariable UUID attendanceId, @RequestBody AttendanceUpsertRequest request) { return service.update(attendanceId, request); }
    @DeleteMapping("/{attendanceId}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable UUID attendanceId) { service.delete(attendanceId); }
}
