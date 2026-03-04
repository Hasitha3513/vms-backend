package genxsolution.vms.vmsbackend.employee_hr_management.payroll.dto;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.UUID;
public record PayrollUpsertRequest(UUID companyId, String companyCode, UUID employeeId, Integer payrollMonth, Integer payrollYear, BigDecimal basicSalary, BigDecimal overtimeHours, BigDecimal overtimeAmount, BigDecimal rationTotal, BigDecimal allowancesTotal, BigDecimal advanceDeductions, BigDecimal otherDeductions, BigDecimal netSalary, LocalDate paymentDate, Integer paymentMethodId, Integer statusId) {}
