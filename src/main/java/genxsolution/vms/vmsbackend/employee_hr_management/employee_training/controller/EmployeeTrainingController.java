package genxsolution.vms.vmsbackend.employee_hr_management.employee_training.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.dto.EmployeeTrainingUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.dto.EmployeeTrainingResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.service.EmployeeTrainingService;
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
@RequestMapping("/api/v1/hr/employee-trainings")
public class EmployeeTrainingController {

    private final EmployeeTrainingService service;

    public EmployeeTrainingController(EmployeeTrainingService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeTrainingResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId"));
    }

    @GetMapping("/{trainingId}")
    public EmployeeTrainingResponse getById(@PathVariable UUID trainingId) {
        return service.getById(trainingId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeTrainingResponse create(@RequestBody EmployeeTrainingUpsertRequest request) {
        return service.create(request);
    }

    @PutMapping("/{trainingId}")
    public EmployeeTrainingResponse update(@PathVariable UUID trainingId, @RequestBody EmployeeTrainingUpsertRequest request) {
        return service.update(trainingId, request);
    }

    @DeleteMapping("/{trainingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID trainingId) {
        service.delete(trainingId);
    }
}









