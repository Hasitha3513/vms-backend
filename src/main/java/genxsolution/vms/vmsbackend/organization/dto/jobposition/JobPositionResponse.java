package genxsolution.vms.vmsbackend.organization.dto.jobposition;

import java.time.Instant;
import java.util.UUID;

public record JobPositionResponse(
        UUID positionId,
        UUID companyId,
        String companyCode,
        String positionCode,
        String positionName,
        String description,
        Boolean isActive,
        Instant createdAt
) {
}






