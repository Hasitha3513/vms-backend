package genxsolution.vms.vmsbackend.organization.dto.companybranch;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CompanyBranchResponse(
        UUID branchId,
        UUID companyId,
        String companyCode,
        String branchCode,
        String branchName,
        String address,
        String city,
        String stateProvince,
        String country,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isMainWorkshop,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {
}






