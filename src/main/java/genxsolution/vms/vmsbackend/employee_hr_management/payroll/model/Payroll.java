package genxsolution.vms.vmsbackend.employee_hr_management.payroll.model;
import java.math.BigDecimal; import java.time.*; import java.util.UUID;
public record Payroll(UUID payrollId, UUID companyId, String companyCode, UUID employeeId, Integer payrollMonth, Integer payrollYear, BigDecimal basicSalary, BigDecimal overtimeHours, BigDecimal overtimeAmount, BigDecimal rationTotal, BigDecimal allowancesTotal, BigDecimal advanceDeductions, BigDecimal otherDeductions, BigDecimal netSalary, LocalDate paymentDate, Integer paymentMethodId, Integer statusId, Instant createdAt) {}
