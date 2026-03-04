package genxsolution.vms.vmsbackend.organization.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.organization.dto.project.CreateProjectRequest;
import genxsolution.vms.vmsbackend.organization.dto.project.ProjectResponse;
import genxsolution.vms.vmsbackend.organization.dto.project.UpdateProjectRequest;
import genxsolution.vms.vmsbackend.organization.dto.dropdown.ProjectManagerOptionResponse;
import genxsolution.vms.vmsbackend.organization.service.OrganizationDropdownService;
import genxsolution.vms.vmsbackend.organization.service.ProjectService;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/projects")
public class ProjectController {

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "companyId", "companyCode", "branchId", "projectCode", "projectName",
            "projectTypeId", "statusId", "projectManager", "startDate",
            "plannedEndDate", "actualEndDate", "budgetAmount", "actualCost"
    );
    private static final Map<String, String> FIELD_ALIASES = Map.ofEntries(
            Map.entry("company_id", "companyId"),
            Map.entry("companycode", "companyCode"),
            Map.entry("company_code", "companyCode"),
            Map.entry("branch_id", "branchId"),
            Map.entry("branchid", "branchId"),
            Map.entry("project_code", "projectCode"),
            Map.entry("projectcode", "projectCode"),
            Map.entry("project_name", "projectName"),
            Map.entry("projectname", "projectName"),
            Map.entry("project_type_id", "projectTypeId"),
            Map.entry("projecttypeid", "projectTypeId"),
            Map.entry("status_id", "statusId"),
            Map.entry("statusid", "statusId"),
            Map.entry("project_manager", "projectManager"),
            Map.entry("projectmanager", "projectManager"),
            Map.entry("start_date", "startDate"),
            Map.entry("planned_end_date", "plannedEndDate"),
            Map.entry("actual_end_date", "actualEndDate"),
            Map.entry("budget_amount", "budgetAmount"),
            Map.entry("actual_cost", "actualCost")
    );

    private final ProjectService service;
    private final OrganizationDropdownService dropdownService;

    public ProjectController(ProjectService service, OrganizationDropdownService dropdownService) {
        this.service = service;
        this.dropdownService = dropdownService;
    }

    @GetMapping
    public List<ProjectResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam Map<String, String> filters
    ) {
        Map<String, String> normalized = normalizeAndValidate(filters);
        return ListQueryEngine.apply(service.list(companyId), normalized, Set.of("companyId"));
    }

    @GetMapping("/{projectId}")
    public ProjectResponse getById(@PathVariable UUID projectId) {
        return service.getById(projectId);
    }

    @GetMapping("/manager-options")
    public List<ProjectManagerOptionResponse> managerOptions(@RequestParam(required = false) UUID companyId) {
        return dropdownService.projectManagerOptions(companyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return service.create(request);
    }

    @PutMapping("/{projectId}")
    public ProjectResponse update(@PathVariable UUID projectId, @Valid @RequestBody UpdateProjectRequest request) {
        return service.update(projectId, request);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID projectId) {
        service.delete(projectId);
    }

    private Map<String, String> normalizeAndValidate(Map<String, String> filters) {
        Map<String, String> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null || value.isBlank()) {
                continue;
            }
            if ("q".equals(key) || "sortDir".equals(key) || "companyId".equals(key)) {
                normalized.put(key, value);
                continue;
            }
            if ("activeOnly".equals(key)) {
                continue;
            }
            if ("sortBy".equals(key)) {
                String sortField = mapAlias(value);
                validateField(sortField);
                normalized.put("sortBy", sortField);
                continue;
            }

            String suffix = "";
            String baseKey = key;
            if (key.endsWith("_like")) {
                suffix = "_like";
                baseKey = key.substring(0, key.length() - 5);
            } else if (key.endsWith("_from")) {
                suffix = "_from";
                baseKey = key.substring(0, key.length() - 5);
            } else if (key.endsWith("_to")) {
                suffix = "_to";
                baseKey = key.substring(0, key.length() - 3);
            }
            String mapped = mapAlias(baseKey);
            validateField(mapped);
            normalized.put(mapped + suffix, value);
        }
        return normalized;
    }

    private String mapAlias(String field) {
        String normalized = field == null ? "" : field.trim().toLowerCase(Locale.ROOT);
        return FIELD_ALIASES.getOrDefault(normalized, field);
    }

    private void validateField(String field) {
        if (!ALLOWED_FILTER_FIELDS.contains(field)) {
            throw new IllegalArgumentException("Allowed project filters/sort: companyId, companyCode, branchId, projectCode, projectName, projectTypeId, statusId, projectManager, startDate, plannedEndDate, actualEndDate, budgetAmount, actualCost");
        }
    }
}







