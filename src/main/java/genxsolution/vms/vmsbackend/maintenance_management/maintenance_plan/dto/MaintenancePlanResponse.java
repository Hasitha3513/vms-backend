package genxsolution.vms.vmsbackend.maintenance_management.maintenance_plan.dto;
import java.util.UUID; import java.time.LocalDate; import java.math.BigDecimal; import java.time.Instant;
public record MaintenancePlanResponse(UUID planId, UUID companyId, String companyCode, UUID vehicleId, String planName, Integer planTypeId, LocalDate startDate, LocalDate endDate, BigDecimal totalEstimatedCost, Integer statusId, UUID createdBy, Instant createdAt) {}
