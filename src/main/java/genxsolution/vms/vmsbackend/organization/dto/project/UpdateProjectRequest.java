package genxsolution.vms.vmsbackend.organization.dto.project;

import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record UpdateProjectRequest(
        UUID branchId,
        @Size(max = 255) String projectName,
        Integer projectTypeId,
        String siteAddress,
        BigDecimal siteLatitude,
        BigDecimal siteLongitude,
        LocalDate startDate,
        LocalDate plannedEndDate,
        LocalDate actualEndDate,
        BigDecimal budgetAmount,
        BigDecimal actualCost,
        @Size(max = 100) String projectManager,
        Integer statusId
) {
}






