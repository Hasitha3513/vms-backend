package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.model.HiredVehicle;
import org.springframework.stereotype.Component;

@Component
public class HiredVehicleMapper {
    public HiredVehicleResponse toResponse(HiredVehicle m) {
        return new HiredVehicleResponse(
                m.hiredVehicleId(),
                m.supplierId(),
                m.supplierCode(),
                m.supplierName(),
                m.hiredVehicleType(),
                m.hiredVehicleTypeName(),
                m.hiredVehicleModel(),
                m.hiredVehicleModelName(),
                m.categoryId(),
                m.categoryName(),
                m.hiredVehicleManufacture(),
                m.hiredVehicleManufactureBrand(),
                m.registrationNumber(),
                m.chassisNumber(),
                m.engineNumber(),
                m.keyNumber(),
                m.vehicleImage(),
                m.manufactureYear(),
                m.color(),
                m.fuelTypeId(),
                m.transmissionTypeId(),
                m.numberPlateTypeId(),
                m.bodyStyleId(),
                m.seatingCapacity(),
                m.undercarriageTypeId(),
                m.engineTypeId(),
                m.engineManufactureId(),
                m.initialOdometerKm(),
                m.currentOdometerKm(),
                m.totalEngineHours(),
                m.consumptionMethodId(),
                m.ratedEfficiencyKmpl(),
                m.ratedConsumptionLph(),
                m.ownershipTypeId(),
                m.ownershipTypeName(),
                m.currentOwnership(),
                m.previousOwnersCount(),
                m.manufactureId(),
                m.distributorId(),
                m.distributorName(),
                m.vehicleCondition(),
                m.operationalStatusId(),
                m.operationalStatusName(),
                m.consumptionMethodName(),
                m.vehicleStatusId(),
                m.notes(),
                m.isActive(),
                m.createdBy(),
                m.updatedBy(),
                m.createdAt(),
                m.updatedAt()
        );
    }
}
