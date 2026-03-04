package genxsolution.vms.vmsbackend.organization.dto.companybranch;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UpdateCompanyBranchRequest(
        @Size(max = 200) String branchName,
        String address,
        @Size(max = 100) String city,
        @Size(max = 100) String stateProvince,
        @Size(max = 100) String country,
        BigDecimal latitude,
        BigDecimal longitude,
        Boolean isMainWorkshop,
        Boolean isActive
) {
}






