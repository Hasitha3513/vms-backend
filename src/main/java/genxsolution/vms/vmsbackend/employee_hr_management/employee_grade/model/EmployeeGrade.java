package genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record EmployeeGrade(
        UUID gradeId,
        UUID companyId,
        String companyCode,
        String gradeCode,
        String gradeName,
        Integer categoryId,
        BigDecimal baseSalary,
        BigDecimal baseAllowance,
        BigDecimal dailyAllowance,
        BigDecimal overtimeRatePerHour,
        String notes,
        Instant createdAt
) {
}







