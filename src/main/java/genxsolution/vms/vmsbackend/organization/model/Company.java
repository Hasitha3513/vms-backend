package genxsolution.vms.vmsbackend.organization.model;

import java.time.Instant;
import java.util.UUID;

public record Company(
        UUID companyId,
        String companyCode,
        String companyName,
        Integer companyTypeId,
        String registrationNo,
        String taxId,
        String email,
        String phonePrimary,
        String address,
        String timezone,
        String currency,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}






