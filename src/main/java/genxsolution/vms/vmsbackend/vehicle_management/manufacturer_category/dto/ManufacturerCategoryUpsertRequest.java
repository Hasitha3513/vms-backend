package genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto;

import java.util.UUID;

public record ManufacturerCategoryUpsertRequest(
        UUID manufacturerId,
        UUID categoryId
) {}
