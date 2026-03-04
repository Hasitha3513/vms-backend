package genxsolution.vms.vmsbackend.employee_hr_management.attendance.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record AttendanceResponse(
        UUID attendanceId, UUID companyId, String companyCode, UUID employeeId, LocalDate attendanceDate,
        Instant checkInTime, Instant checkOutTime, UUID projectId, BigDecimal latitudeIn, BigDecimal longitudeIn,
        BigDecimal latitudeOut, BigDecimal longitudeOut, BigDecimal scheduledHours, BigDecimal actualHours,
        BigDecimal overtimeHours, Integer statusId
) {}
