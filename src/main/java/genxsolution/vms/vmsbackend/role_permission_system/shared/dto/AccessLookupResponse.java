package genxsolution.vms.vmsbackend.role_permission_system.shared.dto;

import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;

import java.util.List;

public record AccessLookupResponse(
        List<LookupOptionDto> loginStatuses,
        List<LookupOptionDto> dataScopes
) {}







