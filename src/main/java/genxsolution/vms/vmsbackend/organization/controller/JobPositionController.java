package genxsolution.vms.vmsbackend.organization.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.organization.dto.jobposition.CreateJobPositionRequest;
import genxsolution.vms.vmsbackend.organization.dto.jobposition.JobPositionResponse;
import genxsolution.vms.vmsbackend.organization.dto.jobposition.UpdateJobPositionRequest;
import genxsolution.vms.vmsbackend.organization.service.JobPositionService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/v1/organizations/job-positions")
public class JobPositionController {

    private final JobPositionService service;

    public JobPositionController(JobPositionService service) {
        this.service = service;
    }

    @GetMapping
    public List<JobPositionResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId, activeOnly), filters, Set.of("companyId", "activeOnly"));
    }

    @GetMapping("/{positionId}")
    public JobPositionResponse getById(@PathVariable UUID positionId) {
        return service.getById(positionId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public JobPositionResponse create(@Valid @RequestBody CreateJobPositionRequest request) {
        return service.create(request);
    }

    @PutMapping("/{positionId}")
    public JobPositionResponse update(@PathVariable UUID positionId, @Valid @RequestBody UpdateJobPositionRequest request) {
        return service.update(positionId, request);
    }

    @DeleteMapping("/{positionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID positionId) {
        service.delete(positionId);
    }
}







