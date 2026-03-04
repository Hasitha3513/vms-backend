package genxsolution.vms.vmsbackend.organization.dto.dropdown;

import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;

import java.util.List;

public record OrganizationLookupResponse(
        List<OrganizationEntityOptionDto> companies,
        List<LookupOptionDto> companyTypes,
        List<LookupOptionDto> projectTypes,
        List<LookupOptionDto> projectStatuses,
        List<String> timezones,
        List<String> currencies
) {
}






