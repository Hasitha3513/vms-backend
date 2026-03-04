package genxsolution.vms.vmsbackend.organization.dto.companybranch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateCompanyBranchRequest(
        @NotNull UUID companyId,
        @NotBlank @Size(max = 50) String companyCode,
        @NotBlank @Size(max = 50) String branchCode,
        @NotBlank @Size(max = 200) String branchName,
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






