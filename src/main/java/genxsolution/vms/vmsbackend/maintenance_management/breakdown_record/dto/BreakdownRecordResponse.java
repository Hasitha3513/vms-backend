package genxsolution.vms.vmsbackend.maintenance_management.breakdown_record.dto;
import java.util.UUID; import java.time.Instant; import java.math.BigDecimal;
public record BreakdownRecordResponse(UUID breakdownId, UUID companyId, String companyCode, UUID vehicleId, UUID driverId, UUID projectId, Instant breakdownAt, String location, BigDecimal odometerKm, Integer breakdownTypeId, Integer severityId, String description, Integer repairCategoryId, Integer repairLocationId, Integer statusId, Instant createdAt) {}
