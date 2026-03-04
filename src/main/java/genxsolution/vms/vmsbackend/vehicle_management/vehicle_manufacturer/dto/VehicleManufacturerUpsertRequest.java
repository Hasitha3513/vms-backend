package genxsolution.vms.vmsbackend.vehicle_management.vehicle_manufacturer.dto;
public record VehicleManufacturerUpsertRequest(String manufacturerName, String manufacturerCode, String manufacturerBrand, String country, String logoUrl, String website, String supportPhone, String supportEmail, String description, Boolean isActive) {}

