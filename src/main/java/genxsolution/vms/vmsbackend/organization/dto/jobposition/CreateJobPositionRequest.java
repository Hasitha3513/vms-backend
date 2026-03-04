package genxsolution.vms.vmsbackend.organization.dto.jobposition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateJobPositionRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max = 50) String companyCode,
        @NotBlank @Size(max = 30) String positionCode,
        @NotBlank @Size(max = 100) String positionName,
        String description,
        Boolean isActive
) {
}






