package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record VehicleModel(
        UUID modelId,
        UUID manufacturerCategoryId,
        UUID categoryId,
        UUID manufacturerId,
        String manufacturerCountry,
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
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}

