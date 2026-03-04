package genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.model;

import java.util.UUID;

public record ManufacturerCategory(
        UUID id,
        UUID manufacturerId,
        UUID categoryId
) {}
