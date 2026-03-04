package genxsolution.vms.vmsbackend.organization.dto.department;

import java.time.Instant;
import java.util.UUID;

public record DepartmentResponse(
        UUID departmentId,
        UUID companyId,
        String companyCode,
        UUID branchId,
        String departmentCode,
        String departmentName,
        UUID parentDepartmentId,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}






