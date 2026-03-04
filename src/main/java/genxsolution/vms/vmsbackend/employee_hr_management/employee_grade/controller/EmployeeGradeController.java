package genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.dto.EmployeeGradeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.dto.EmployeeGradeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.service.EmployeeGradeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hr/employee-grades")
public class EmployeeGradeController {

    private final EmployeeGradeService service;

    public EmployeeGradeController(EmployeeGradeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeGradeResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId"));
    }

    @GetMapping("/{gradeId}")
    public EmployeeGradeResponse getById(@PathVariable UUID gradeId) {
        return service.getById(gradeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeGradeResponse create(@RequestBody EmployeeGradeUpsertRequest request) {
        return service.create(request);
    }

    @PutMapping("/{gradeId}")
    public EmployeeGradeResponse update(@PathVariable UUID gradeId, @RequestBody EmployeeGradeUpsertRequest request) {
        return service.update(gradeId, request);
    }

    @DeleteMapping("/{gradeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID gradeId) {
        service.delete(gradeId);
    }
}









