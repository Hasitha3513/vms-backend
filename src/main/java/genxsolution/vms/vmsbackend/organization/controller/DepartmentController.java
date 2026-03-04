package genxsolution.vms.vmsbackend.organization.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.organization.dto.department.CreateDepartmentRequest;
import genxsolution.vms.vmsbackend.organization.dto.department.DepartmentResponse;
import genxsolution.vms.vmsbackend.organization.dto.department.UpdateDepartmentRequest;
import genxsolution.vms.vmsbackend.organization.service.DepartmentService;
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
@RequestMapping("/api/v1/organizations/departments")
public class DepartmentController {

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "companyId", "companyCode", "branchId", "departmentCode",
            "departmentName", "parentDepartmentId", "active"
    );
    private static final Map<String, String> FIELD_ALIASES = Map.ofEntries(
            Map.entry("company_id", "companyId"),
            Map.entry("companycode", "companyCode"),
            Map.entry("company_code", "companyCode"),
            Map.entry("branch_id", "branchId"),
            Map.entry("branchid", "branchId"),
            Map.entry("department_code", "departmentCode"),
            Map.entry("departmentcode", "departmentCode"),
            Map.entry("department_name", "departmentName"),
            Map.entry("departmentname", "departmentName"),
            Map.entry("parent_department_id", "parentDepartmentId"),
            Map.entry("parentdepartmentid", "parentDepartmentId"),
            Map.entry("is_active", "active"),
            Map.entry("isactive", "active")
    );

    private final DepartmentService service;

    public DepartmentController(DepartmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<DepartmentResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam Map<String, String> filters
    ) {
        Map<String, String> normalized = normalizeAndValidate(filters);
        return ListQueryEngine.apply(service.list(companyId, activeOnly), normalized, Set.of("companyId", "activeOnly"));
    }

    @GetMapping("/{departmentId}")
    public DepartmentResponse getById(@PathVariable UUID departmentId) {
        return service.getById(departmentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody CreateDepartmentRequest request) {
        return service.create(request);
    }

    @PutMapping("/{departmentId}")
    public DepartmentResponse update(@PathVariable UUID departmentId, @Valid @RequestBody UpdateDepartmentRequest request) {
        return service.update(departmentId, request);
    }

    @DeleteMapping("/{departmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID departmentId) {
        service.delete(departmentId);
    }

    private Map<String, String> normalizeAndValidate(Map<String, String> filters) {
        Map<String, String> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null || value.isBlank()) {
                continue;
            }
            if ("activeOnly".equals(key) || "q".equals(key) || "sortDir".equals(key) || "companyId".equals(key)) {
                normalized.put(key, value);
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
            throw new IllegalArgumentException("Allowed department filters/sort: companyId, companyCode, branchId, departmentCode, departmentName, parentDepartmentId, active");
        }
    }
}







