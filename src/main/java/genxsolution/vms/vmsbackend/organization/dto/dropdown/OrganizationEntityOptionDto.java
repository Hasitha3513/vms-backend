package genxsolution.vms.vmsbackend.organization.dto.dropdown;

import java.util.UUID;

public record OrganizationEntityOptionDto(
        UUID id,
        String code,
        String name
) {
}

