package genxsolution.vms.vmsbackend.organization.dto.dropdown;

import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;

import java.util.List;
import java.util.UUID;

public record OrganizationUserContextLookupResponse(
        UUID companyId,
        String companyCode,
        String companyName,
        List<OrganizationEntityOptionDto> branches,
        List<OrganizationEntityOptionDto> departments,
        List<OrganizationEntityOptionDto> projects,
        List<LookupOptionDto> companyTypes,
        List<LookupOptionDto> projectTypes
) {
}

