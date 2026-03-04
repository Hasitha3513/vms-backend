package genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record OvertimeRequest(
        UUID overtimeId, UUID companyId, String companyCode, UUID employeeId, UUID projectId,
        LocalDate otDate, BigDecimal hours, Integer otTypeId, Boolean approved, UUID approvedBy,
        Instant approvedAt, Instant createdAt
) {}
