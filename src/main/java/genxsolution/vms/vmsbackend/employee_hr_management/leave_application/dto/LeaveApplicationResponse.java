package genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record LeaveApplicationResponse(
        UUID leaveId, UUID companyId, String companyCode, UUID employeeId, UUID leaveTypeId,
        LocalDate startDate, LocalDate endDate, BigDecimal totalDays, Integer statusId,
        Instant appliedAt, UUID approvedBy, Instant approvedAt, Instant updatedAt
) {}
