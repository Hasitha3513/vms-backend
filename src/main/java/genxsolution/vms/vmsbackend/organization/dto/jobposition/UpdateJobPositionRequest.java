package genxsolution.vms.vmsbackend.organization.dto.jobposition;

import jakarta.validation.constraints.Size;

public record UpdateJobPositionRequest(
        @Size(max = 100) String positionName,
        String description,
        Boolean isActive
) {
}






