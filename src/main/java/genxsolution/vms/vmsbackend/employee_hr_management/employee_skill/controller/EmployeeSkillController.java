package genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.dto.EmployeeSkillUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.dto.EmployeeSkillResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.service.EmployeeSkillService;
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
@RequestMapping("/api/v1/hr/employee-skills")
public class EmployeeSkillController {

    private final EmployeeSkillService service;

    public EmployeeSkillController(EmployeeSkillService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeSkillResponse> list(
            @RequestParam(required = false) String companyId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Integer limit,
            @RequestParam Map<String, String> filters
    ) {
        UUID companyUuid = parseUuid(companyId);
        int safeLimit = limit == null ? 100 : Math.max(1, Math.min(limit, 500));
        return ListQueryEngine.apply(
                service.list(companyUuid, q, safeLimit, filters),
                filters,
                Set.of("companyId", "q", "limit", "skillCategoryId", "activeOnly")
        );
    }

    @GetMapping("/{skillId}")
    public EmployeeSkillResponse getById(@PathVariable UUID skillId) {
        return service.getById(skillId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeSkillResponse create(@RequestBody EmployeeSkillUpsertRequest request) {
        return service.create(request);
    }

    @PutMapping("/{skillId}")
    public EmployeeSkillResponse update(@PathVariable UUID skillId, @RequestBody EmployeeSkillUpsertRequest request) {
        return service.update(skillId, request);
    }

    @DeleteMapping("/{skillId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID skillId) {
        service.delete(skillId);
    }

    private UUID parseUuid(String value) {
        if (value == null) return null;
        String s = value.trim();
        if (s.isEmpty() || "undefined".equalsIgnoreCase(s) || "null".equalsIgnoreCase(s)) return null;
        try {
            return UUID.fromString(s);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}








