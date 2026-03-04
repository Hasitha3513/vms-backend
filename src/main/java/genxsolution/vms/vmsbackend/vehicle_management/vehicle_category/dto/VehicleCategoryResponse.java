package genxsolution.vms.vmsbackend.vehicle_management.vehicle_category.dto;
import java.time.Instant;
import java.util.UUID;
public record VehicleCategoryResponse(UUID categoryId, String categoryName, String categoryCode, Integer categoryTypeId, String description, String iconUrl, Boolean isActive, Instant createdAt, Instant updatedAt) {}

