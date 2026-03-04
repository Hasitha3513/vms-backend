package genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record EmployeeGradeUpsertRequest(
        UUID companyId,
        String companyCode,
        String gradeCode,
        String gradeName,
        Integer categoryId,
        BigDecimal baseSalary,
        BigDecimal baseAllowance,
        BigDecimal dailyAllowance,
        BigDecimal overtimeRatePerHour,
        String notes
) {
}







