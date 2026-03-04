package genxsolution.vms.vmsbackend.vehicle_management.vehicle_manufacturer.model;
import java.time.Instant;
import java.util.UUID;
public record VehicleManufacturer(UUID manufacturerId, String manufacturerName, String manufacturerCode, String manufacturerBrand, String country, String logoUrl, String website, String supportPhone, String supportEmail, String description, Boolean isActive, Instant createdAt, Instant updatedAt) {}

