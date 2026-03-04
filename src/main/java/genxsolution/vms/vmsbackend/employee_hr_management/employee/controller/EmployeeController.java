package genxsolution.vms.vmsbackend.employee_hr_management.employee.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.dto.EmployeeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.dto.EmployeeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.service.EmployeeService;
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
@RequestMapping("/api/v1/hr/employees")
public class EmployeeController {

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "companyId", "companyCode", "branchId", "departmentId", "employeeCode",
            "firstName", "lastName", "genderId", "nationalId",
            "mobilePhone", "employmentTypeId", "jobTitle", "isDriver",
            "isOperator", "isTechnician", "employmentStatusId", "roleType"
    );

    private static final Map<String, String> FIELD_ALIASES = Map.ofEntries(
            Map.entry("company_id", "companyId"),
            Map.entry("companyid", "companyId"),
            Map.entry("company_code", "companyCode"),
            Map.entry("companycode", "companyCode"),
            Map.entry("branch_id", "branchId"),
            Map.entry("branchid", "branchId"),
            Map.entry("department_id", "departmentId"),
            Map.entry("departmentid", "departmentId"),
            Map.entry("employee_code", "employeeCode"),
            Map.entry("employeecode", "employeeCode"),
            Map.entry("first_name", "firstName"),
            Map.entry("firstname", "firstName"),
            Map.entry("last_name", "lastName"),
            Map.entry("lastname", "lastName"),
            Map.entry("gender_id", "genderId"),
            Map.entry("genderid", "genderId"),
            Map.entry("national_id", "nationalId"),
            Map.entry("nationalid", "nationalId"),
            Map.entry("mobile_phone", "mobilePhone"),
            Map.entry("mobilephone", "mobilePhone"),
            Map.entry("employee_type_id", "employmentTypeId"),
            Map.entry("employee_type", "employmentTypeId"),
            Map.entry("employment_type_id", "employmentTypeId"),
            Map.entry("employmenttypeid", "employmentTypeId"),
            Map.entry("job_title", "jobTitle"),
            Map.entry("jobtitle", "jobTitle"),
            Map.entry("isdriver", "isDriver"),
            Map.entry("is_driver", "isDriver"),
            Map.entry("isoperator", "isOperator"),
            Map.entry("is_operator", "isOperator"),
            Map.entry("istechnician", "isTechnician"),
            Map.entry("is_technician", "isTechnician"),
            Map.entry("roletype", "roleType"),
            Map.entry("role_type", "roleType"),
            Map.entry("employee_role", "roleType"),
            Map.entry("employee_status_id", "employmentStatusId"),
            Map.entry("employment_status_id", "employmentStatusId"),
            Map.entry("employmentstatusid", "employmentStatusId"),
            Map.entry("status", "employmentStatusId"),
            Map.entry("statusid", "employmentStatusId")
    );

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<EmployeeResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam Map<String, String> filters
    ) {
        Map<String, String> normalized = normalizeAndValidate(filters);
        return ListQueryEngine.apply(service.list(companyId), normalized, Set.of());
    }

    @GetMapping("/{employeeId}")
    public EmployeeResponse getById(@PathVariable UUID employeeId) {
        return service.getById(employeeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@RequestBody EmployeeUpsertRequest request) {
        return service.create(request);
    }

    @PutMapping("/{employeeId}")
    public EmployeeResponse update(@PathVariable UUID employeeId, @RequestBody EmployeeUpsertRequest request) {
        return service.update(employeeId, request);
    }

    @DeleteMapping("/{employeeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID employeeId) {
        service.delete(employeeId);
    }

    private Map<String, String> normalizeAndValidate(Map<String, String> filters) {
        Map<String, String> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : filters.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key == null || value == null || value.isBlank()) {
                continue;
            }

            if ("q".equals(key) || "sortDir".equals(key)) {
                normalized.put(key, value);
                continue;
            }

            if ("name".equalsIgnoreCase(key) || "fullName".equalsIgnoreCase(key) || "employeeName".equalsIgnoreCase(key)) {
                normalized.put("q", value);
                continue;
            }
            if ("roleType".equalsIgnoreCase(key) || "role_type".equalsIgnoreCase(key) || "employee_role".equalsIgnoreCase(key)) {
                String role = value.trim().toLowerCase(Locale.ROOT);
                if ("driver".equals(role)) {
                    normalized.put("isDriver", "true");
                } else if ("operator".equals(role)) {
                    normalized.put("isOperator", "true");
                } else if ("technician".equals(role)) {
                    normalized.put("isTechnician", "true");
                }
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
            throw new IllegalArgumentException(
                    "Allowed employee filters/sort: companyId, companyCode, branchId, departmentId, employeeCode, firstName, lastName, genderId, nationalId, mobilePhone, employmentTypeId, jobTitle, isDriver, isOperator, isTechnician, employmentStatusId, roleType"
            );
        }
    }
}








