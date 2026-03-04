package genxsolution.vms.vmsbackend.organization.service;

import genxsolution.vms.vmsbackend.authentication.AuthContext;
import genxsolution.vms.vmsbackend.authentication.AuthContextHolder;
import genxsolution.vms.vmsbackend.common.cache.CacheNames;
import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;
import genxsolution.vms.vmsbackend.lookup.service.LookupEnumService;
import genxsolution.vms.vmsbackend.organization.dto.dropdown.OrganizationEntityOptionDto;
import genxsolution.vms.vmsbackend.organization.dto.dropdown.OrganizationLookupResponse;
import genxsolution.vms.vmsbackend.organization.dto.dropdown.ProjectManagerOptionResponse;
import genxsolution.vms.vmsbackend.organization.dto.dropdown.OrganizationUserContextLookupResponse;
import genxsolution.vms.vmsbackend.organization.repository.CompanyBranchRepository;
import genxsolution.vms.vmsbackend.organization.repository.CompanyRepository;
import genxsolution.vms.vmsbackend.organization.repository.DepartmentRepository;
import genxsolution.vms.vmsbackend.organization.repository.ProjectRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
public class OrganizationDropdownService {

    private final LookupEnumService lookupEnumService;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository companyBranchRepository;
    private final DepartmentRepository departmentRepository;
    private final ProjectRepository projectRepository;
    private final JdbcTemplate jdbcTemplate;

    public OrganizationDropdownService(
            LookupEnumService lookupEnumService,
            CompanyRepository companyRepository,
            CompanyBranchRepository companyBranchRepository,
            DepartmentRepository departmentRepository,
            ProjectRepository projectRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.lookupEnumService = lookupEnumService;
        this.companyRepository = companyRepository;
        this.companyBranchRepository = companyBranchRepository;
        this.departmentRepository = departmentRepository;
        this.projectRepository = projectRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Cacheable(cacheNames = CacheNames.ORG_DROPDOWN_CORE, key = "#activeOnly")
    public OrganizationLookupResponse coreOrganizationLookups(boolean activeOnly) {
        return new OrganizationLookupResponse(
                companyRepository.findAll(activeOnly).stream()
                        .map(c -> new OrganizationEntityOptionDto(c.companyId(), c.companyCode(), c.companyName()))
                        .toList(),
                lookupEnumService.getEnumValues("company_type", activeOnly),
                lookupEnumService.getEnumValues("project_type", activeOnly),
                lookupEnumService.getEnumValues("project_status", activeOnly),
                ZoneId.getAvailableZoneIds().stream().sorted().toList(),
                Currency.getAvailableCurrencies().stream()
                        .map(Currency::getCurrencyCode)
                        .sorted()
                        .toList()
        );
    }

    @Cacheable(cacheNames = CacheNames.ORG_DROPDOWN_ME, key = "@tenantKeyResolver.tenant() + ':' + #activeOnly")
    public OrganizationUserContextLookupResponse userContextLookups(boolean activeOnly) {
        AuthContext context = AuthContextHolder.get();
        String companyCode = context == null ? null : context.companyCode();
        if (companyCode == null || companyCode.isBlank()) {
            return new OrganizationUserContextLookupResponse(
                    null,
                    null,
                    null,
                    List.of(),
                    List.of(),
                    List.of(),
                    lookupEnumService.getEnumValues("company_type", activeOnly),
                    lookupEnumService.getEnumValues("project_type", activeOnly)
            );
        }

        return companyRepository.findByCode(companyCode)
                .map(company -> {
                    UUID companyId = company.companyId();
                    List<OrganizationEntityOptionDto> branchOptions = companyBranchRepository.findAllByCompany(companyId, activeOnly)
                            .stream()
                            .map(b -> new OrganizationEntityOptionDto(b.branchId(), b.branchCode(), b.branchName()))
                            .toList();
                    List<OrganizationEntityOptionDto> departmentOptions = departmentRepository.findAllByCompany(companyId, activeOnly)
                            .stream()
                            .map(d -> new OrganizationEntityOptionDto(d.departmentId(), d.departmentCode(), d.departmentName()))
                            .toList();
                    List<OrganizationEntityOptionDto> projectOptions = projectRepository.findAllByCompany(companyId)
                            .stream()
                            .map(p -> new OrganizationEntityOptionDto(p.projectId(), p.projectCode(), p.projectName()))
                            .toList();
                    List<LookupOptionDto> companyTypes = lookupEnumService.getEnumValues("company_type", activeOnly);
                    List<LookupOptionDto> projectTypes = lookupEnumService.getEnumValues("project_type", activeOnly);

                    return new OrganizationUserContextLookupResponse(
                            companyId,
                            company.companyCode(),
                            company.companyName(),
                            branchOptions,
                            departmentOptions,
                            projectOptions,
                            companyTypes,
                            projectTypes
                    );
                })
                .orElseGet(() -> new OrganizationUserContextLookupResponse(
                        null,
                        companyCode,
                        null,
                        List.of(),
                        List.of(),
                        List.of(),
                        lookupEnumService.getEnumValues("company_type", activeOnly),
                        lookupEnumService.getEnumValues("project_type", activeOnly)
                ));
    }

    public List<ProjectManagerOptionResponse> projectManagerOptions(UUID companyId) {
        String baseSelect = """
                SELECT employee_id, company_id, employee_code,
                       NULLIF(TRIM(CONCAT(COALESCE(first_name, ''), ' ', COALESCE(last_name, ''))), '') AS employee_name
                FROM employee
                """;
        String ordered = " ORDER BY employee_name NULLS LAST, employee_code";
        var mapper = (org.springframework.jdbc.core.RowMapper<ProjectManagerOptionResponse>) (rs, rowNum) -> {
            UUID employeeId = rs.getObject("employee_id", UUID.class);
            UUID employeeCompanyId = rs.getObject("company_id", UUID.class);
            String employeeCode = rs.getString("employee_code");
            String employeeName = rs.getString("employee_name");
            if (employeeName == null || employeeName.isBlank()) {
                employeeName = employeeCode == null ? "Employee" : employeeCode;
            }
            String codeLabel = employeeCode == null || employeeCode.isBlank() ? "" : " (" + employeeCode + ")";
            return new ProjectManagerOptionResponse(
                    employeeId,
                    employeeCompanyId,
                    employeeCode,
                    employeeName,
                    employeeName + codeLabel
            );
        };

        if (companyId == null) {
            return jdbcTemplate.query(baseSelect + ordered, mapper);
        }
        return jdbcTemplate.query(baseSelect + " WHERE company_id = ?" + ordered, mapper, companyId);
    }
}






