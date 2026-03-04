package genxsolution.vms.vmsbackend.vehicle_management.vehicle.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle.dto.VehicleResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle.model.Vehicle;
import org.springframework.stereotype.Component;

@Component
public class VehicleMapper {
    public VehicleResponse toResponse(Vehicle m) {
        return new VehicleResponse(
                m.vehicleId(), m.companyId(), m.companyCode(), m.branchId(), m.modelId(), m.variantId(), m.vehicleCode(),
                m.registrationNumber(), m.chassisNumber(), m.engineNumber(), m.keyNumber(), m.ownershipTypeId(),
                m.currentOwnership(),
                m.manufactureYear(), m.registeredYear(), m.country(), m.color(), m.initialOdometerKm(),
                m.currentOdometerKm(), m.totalEngineHours(), m.consumptionMethodId(), m.airCondition(),
                m.ratedEfficiencyKmpl(), m.ratedConsumptionLph(), m.operationalStatusId(), m.currentLocation(),
                m.currentProjectId(), m.currentDriverId(), m.insuranceExpiry(), m.registrationExpiry(), m.notes(),
                m.isActive(), m.decommissionDate(), m.decommissionReason(), m.createdAt(), m.updatedAt(),
                m.typeId(), m.categoryId(), m.fuelTypeId(), m.transmissionTypeId(), m.numberPlateTypeId(),
                m.bodyStyleId(), m.seatingCapacity(), m.undercarriageTypeId(), m.engineTypeId(),
                m.engineManufactureId(), m.previousOwnersCount(), m.manufacturerId(), m.distributorId(),
                m.vehicleCondition(), m.vehicleStatusId(), m.vehicleImage(), m.createdBy(), m.updatedBy()
        );
    }
}
