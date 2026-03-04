package genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto;

import java.util.UUID;

public record ManufacturerCategoryResponse(
        UUID id,
        UUID manufacturerId,
        UUID categoryId
) {}
