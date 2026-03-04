package genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record OvertimeRequestResponse(
        UUID overtimeId, UUID companyId, String companyCode, UUID employeeId, UUID projectId,
        LocalDate otDate, BigDecimal hours, Integer otTypeId, Boolean approved, UUID approvedBy,
        Instant approvedAt, Instant createdAt
) {}
