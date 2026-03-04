package genxsolution.vms.vmsbackend.organization.dto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateProjectRequest(
        @NotNull UUID companyId,
        @Size(max = 50) String companyCode,
        UUID branchId,
        @NotBlank @Size(max = 80) String projectCode,
        @NotBlank @Size(max = 255) String projectName,
        Integer projectTypeId,
        String siteAddress,
        BigDecimal siteLatitude,
        BigDecimal siteLongitude,
        @NotNull LocalDate startDate,
        LocalDate plannedEndDate,
        LocalDate actualEndDate,
        BigDecimal budgetAmount,
        BigDecimal actualCost,
        @Size(max = 100) String projectManager,
        Integer statusId
) {
}






