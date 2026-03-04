package genxsolution.vms.vmsbackend.employee_hr_management.employee_advance.model;
import java.math.BigDecimal; import java.time.*; import java.util.UUID;
public record EmployeeAdvance(UUID advanceId, UUID companyId, String companyCode, UUID employeeId, LocalDate issuedDate, BigDecimal amount, BigDecimal balance, String purpose, Integer statusId, Instant createdAt) {}
