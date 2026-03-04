package genxsolution.vms.vmsbackend.organization.controller;

import genxsolution.vms.vmsbackend.organization.dto.dropdown.OrganizationLookupResponse;
import genxsolution.vms.vmsbackend.organization.dto.dropdown.ProjectManagerOptionResponse;
import genxsolution.vms.vmsbackend.organization.dto.dropdown.OrganizationUserContextLookupResponse;
import genxsolution.vms.vmsbackend.organization.service.OrganizationDropdownService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/organizations/dropdowns")
public class OrganizationDropdownController {

    private final OrganizationDropdownService dropdownService;

    public OrganizationDropdownController(OrganizationDropdownService dropdownService) {
        this.dropdownService = dropdownService;
    }

    @GetMapping("/core")
    public OrganizationLookupResponse coreLookups(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return dropdownService.coreOrganizationLookups(activeOnly);
    }

    @GetMapping("/me")
    public OrganizationUserContextLookupResponse currentUserLookups(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return dropdownService.userContextLookups(activeOnly);
    }

    @GetMapping("/project-managers")
    public List<ProjectManagerOptionResponse> projectManagers(@RequestParam(required = false) UUID companyId) {
        return dropdownService.projectManagerOptions(companyId);
    }
}






