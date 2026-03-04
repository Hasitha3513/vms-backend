package genxsolution.vms.vmsbackend.organization.dto.project;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ProjectResponse(
        UUID projectId,
        UUID companyId,
        String companyCode,
        UUID branchId,
        String projectCode,
        String projectName,
        Integer projectTypeId,
        String siteAddress,
        BigDecimal siteLatitude,
        BigDecimal siteLongitude,
        LocalDate startDate,
        LocalDate plannedEndDate,
        LocalDate actualEndDate,
        BigDecimal budgetAmount,
        BigDecimal actualCost,
        String projectManager,
        Integer statusId,
        Instant createdAt,
        Instant updatedAt
) {
}






