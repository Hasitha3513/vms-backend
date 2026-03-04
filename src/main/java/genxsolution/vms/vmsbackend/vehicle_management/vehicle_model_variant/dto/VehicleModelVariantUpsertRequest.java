package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VehicleModelVariantUpsertRequest(
        UUID modelId,
        String variantName,
        String variantCode,
        String additionalFeatures,
        BigDecimal priceExShowroom,
        Boolean isActive
) {}
