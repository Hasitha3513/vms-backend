package genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LeaveApplicationUpsertRequest(
        UUID companyId, String companyCode, UUID employeeId, UUID leaveTypeId,
        LocalDate startDate, LocalDate endDate, BigDecimal totalDays, Integer statusId,
        UUID approvedBy, Instant approvedAt
) {}
