package genxsolution.vms.vmsbackend.organization.model;

import java.time.Instant;
import java.util.UUID;

public record JobPosition(
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






