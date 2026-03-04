package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record VehicleModelUpsertRequest(
        UUID manufacturerCategoryId,
        UUID categoryId,
        UUID manufacturerId,
        UUID typeId,
        String modelName,
        String modelCode,
        Integer modelYear,
        Integer bodyStyleId,
        Integer fuelTypeId,
        Integer engineCapacityCc,
        Integer powerHp,
        Integer torqueNm,
        Integer numberOfCylinders,
        Integer transmissionTypeId,
        Integer drivetrainTypeId,
        Integer seatingCapacity,
        Integer numberOfDoors,
        BigDecimal kerbWeightKg,
        BigDecimal gvwKg,
        Integer wheelbaseMm,
        BigDecimal fuelEfficiencyKmpl,
        Integer launchYear,
        String description,
        String imageUrl,
        Boolean isActive
) {}

