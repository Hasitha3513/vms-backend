package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record HiredVehicleModelPrefillResponse(
        UUID modelId,
        UUID typeId,
        UUID categoryId,
        UUID manufacturerId,
        Integer ownershipTypeId,
        String currentOwnership,
        BigDecimal initialOdometerKm,
        BigDecimal currentOdometerKm,
        BigDecimal totalEngineHours,
        Integer consumptionMethodId,
        BigDecimal ratedEfficiencyKmpl,
        BigDecimal ratedConsumptionLph
) {}
