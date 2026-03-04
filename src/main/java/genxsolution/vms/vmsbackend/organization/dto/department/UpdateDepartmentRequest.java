package genxsolution.vms.vmsbackend.organization.dto.department;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateDepartmentRequest(
        UUID branchId,
        @Size(max = 120) String departmentName,
        UUID parentDepartmentId,
        Boolean isActive
) {
}






