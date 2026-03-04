package genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.dto;
import java.util.UUID; import java.time.Instant;
public record VehicleTypeResponse(UUID typeId, UUID categoryId, String typeName, Integer bodyStyleId, Integer fuelTypeId, Integer undercarriageTypeId, Integer numberOfWheels, Integer seatingCapacityMin, Integer seatingCapacityMax, String usageType, Integer serviceIntervalKm, Integer serviceIntervalMonths, Integer serviceIntervalHours, Integer oilChangeIntervalKm, String description, Boolean isActive, Instant createdAt, Instant updatedAt) {}

