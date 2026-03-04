package genxsolution.vms.vmsbackend.employee_hr_management.attendance.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record Attendance(
        UUID attendanceId,
        UUID companyId,
        String companyCode,
        UUID employeeId,
        LocalDate attendanceDate,
        Instant checkInTime,
        Instant checkOutTime,
        UUID projectId,
        BigDecimal latitudeIn,
        BigDecimal longitudeIn,
        BigDecimal latitudeOut,
        BigDecimal longitudeOut,
        BigDecimal scheduledHours,
        BigDecimal actualHours,
        BigDecimal overtimeHours,
        Integer statusId
) {
}
