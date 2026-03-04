package genxsolution.vms.vmsbackend.maintenance_management.maintenance_plan_item.model;
import java.util.UUID; import java.time.LocalDate; import java.math.BigDecimal; import java.time.Instant;
public record MaintenancePlanItem(UUID planItemId, UUID companyId, String companyCode, UUID planId, UUID standardId, String itemDescription, LocalDate scheduledDate, BigDecimal estimatedCost, BigDecimal actualCost, Integer statusId, LocalDate completedDate, String notes, Instant createdAt) {}
