package genxsolution.vms.vmsbackend.employee_hr_management.ration_policy.model;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record RationPolicy(UUID rationPolicyId, UUID companyId, String companyCode, String policyName, BigDecimal perDayAmount, String notes, Boolean isActive, Instant createdAt) {}
