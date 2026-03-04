package genxsolution.vms.vmsbackend.vehicle_management.vehicle_assignment.dto;
import java.util.UUID; import java.time.Instant; import java.math.BigDecimal;
public record VehicleAssignmentResponse(UUID assignmentId, UUID companyId, String companyCode, UUID vehicleId, Integer assignmentTypeId, UUID assignedToEmployeeId, UUID assignedToProjectId, Instant assignedAt, Instant expectedReturnAt, Instant returnedAt, BigDecimal startOdometerKm, BigDecimal endOdometerKm, BigDecimal startFuelLevelPercent, BigDecimal endFuelLevelPercent, Integer statusId, Instant createdAt) {}
