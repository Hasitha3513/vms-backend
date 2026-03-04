package genxsolution.vms.vmsbackend.organization.dto.dropdown;

import java.util.UUID;

public record ProjectManagerOptionResponse(
        UUID employeeId,
        UUID companyId,
        String employeeCode,
        String employeeName,
        String displayName
) {
}
