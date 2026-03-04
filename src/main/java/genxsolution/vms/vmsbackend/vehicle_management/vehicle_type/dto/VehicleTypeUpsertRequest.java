package genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.dto;
import java.util.UUID;
public record VehicleTypeUpsertRequest(UUID categoryId, String typeName, Integer bodyStyleId, Integer fuelTypeId, Integer undercarriageTypeId, Integer numberOfWheels, Integer seatingCapacityMin, Integer seatingCapacityMax, String usageType, Integer serviceIntervalKm, Integer serviceIntervalMonths, Integer serviceIntervalHours, Integer oilChangeIntervalKm, String description, Boolean isActive) {}

