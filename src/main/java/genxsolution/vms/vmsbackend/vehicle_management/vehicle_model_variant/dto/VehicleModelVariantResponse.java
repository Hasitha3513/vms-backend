package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VehicleModelVariantResponse(
        UUID variantId,
        UUID modelId,
        String variantName,
        String variantCode,
        String additionalFeatures,
        BigDecimal priceExShowroom,
        Boolean isActive,
        Instant createdAt
) {}
