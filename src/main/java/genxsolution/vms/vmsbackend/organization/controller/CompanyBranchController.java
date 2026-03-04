package genxsolution.vms.vmsbackend.organization.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.organization.dto.companybranch.CompanyBranchResponse;
import genxsolution.vms.vmsbackend.organization.dto.companybranch.CreateCompanyBranchRequest;
import genxsolution.vms.vmsbackend.organization.dto.companybranch.UpdateCompanyBranchRequest;
import genxsolution.vms.vmsbackend.organization.service.CompanyBranchService;
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
@RequestMapping("/api/v1/organizations/branches")
public class CompanyBranchController {

    private static final Set<String> ALLOWED_FILTER_FIELDS = Set.of(
            "companyId", "companyCode", "branchCode", "branchName", "city",
            "stateProvince", "country", "mainWorkshop", "active"
    );
    private static final Map<String, String> FIELD_ALIASES = Map.ofEntries(
            Map.entry("company_id", "companyId"),
            Map.entry("companycode", "companyCode"),
            Map.entry("company_code", "companyCode"),
            Map.entry("branch_code", "branchCode"),
            Map.entry("branchcode", "branchCode"),
            Map.entry("branch_name", "branchName"),
            Map.entry("branchname", "branchName"),
            Map.entry("state_province", "stateProvince"),
            Map.entry("main_workshop", "mainWorkshop"),
            Map.entry("is_main_workshop", "mainWorkshop"),
            Map.entry("ismainworkshop", "mainWorkshop"),
            Map.entry("is_active", "active"),
            Map.entry("isactive", "active")
    );

    private final CompanyBranchService service;

    public CompanyBranchController(CompanyBranchService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompanyBranchResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(defaultValue = "true") boolean activeOnly,
            @RequestParam Map<String, String> filters
    ) {
        Map<String, String> normalized = normalizeAndValidate(filters);
        return ListQueryEngine.apply(service.list(companyId, activeOnly), normalized, Set.of("companyId", "activeOnly"));
    }

    @GetMapping("/{branchId}")
    public CompanyBranchResponse getById(@PathVariable UUID branchId) {
        return service.getById(branchId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyBranchResponse create(@Valid @RequestBody CreateCompanyBranchRequest request) {
        return service.create(request);
    }

    @PutMapping("/{branchId}")
    public CompanyBranchResponse update(@PathVariable UUID branchId, @Valid @RequestBody UpdateCompanyBranchRequest request) {
        return service.update(branchId, request);
    }

    @DeleteMapping("/{branchId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID branchId) {
        service.delete(branchId);
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
            throw new IllegalArgumentException("Allowed branch filters/sort: companyId, companyCode, branchCode, branchName, city, stateProvince, country, mainWorkshop, active");
        }
    }
}







