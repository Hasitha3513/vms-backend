package genxsolution.vms.vmsbackend.vehicle_management.vehicle_category.dto;
public record VehicleCategoryUpsertRequest(String categoryName, String categoryCode, Integer categoryTypeId, String description, String iconUrl, Boolean isActive) {}

