package genxsolution.vms.vmsbackend.employee_hr_management.payroll_deduction.dto;
import java.math.BigDecimal; import java.util.UUID;
public record PayrollDeductionResponse(UUID payDedId, UUID companyId, String companyCode, UUID payrollId, Integer deductionTypeId, UUID referenceId, BigDecimal amount, String note) {}
