package genxsolution.vms.vmsbackend.employee_hr_management.ration_distribution.model;
import java.math.BigDecimal; import java.time.*; import java.util.UUID;
public record RationDistribution(UUID rationId, UUID companyId, String companyCode, UUID employeeId, UUID projectId, LocalDate rationDate, Integer mealsCount, BigDecimal amount, String notes, Instant createdAt) {}
