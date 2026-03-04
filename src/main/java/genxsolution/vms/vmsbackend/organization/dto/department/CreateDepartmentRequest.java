package genxsolution.vms.vmsbackend.organization.dto.department;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateDepartmentRequest(
        @NotNull UUID companyId,
        @Size(max = 50) String companyCode,
        UUID branchId,
        @NotBlank @Size(max = 50) String departmentCode,
        @NotBlank @Size(max = 120) String departmentName,
        UUID parentDepartmentId,
        Boolean isActive
) {
}






