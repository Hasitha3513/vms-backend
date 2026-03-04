package genxsolution.vms.vmsbackend.vehicle_management.vehicle_manufacturer.dto;
import java.time.Instant;
import java.util.UUID;
public record VehicleManufacturerResponse(UUID manufacturerId, String manufacturerName, String manufacturerCode, String manufacturerBrand, String country, String logoUrl, String website, String supportPhone, String supportEmail, String description, Boolean isActive, Instant createdAt, Instant updatedAt) {}

