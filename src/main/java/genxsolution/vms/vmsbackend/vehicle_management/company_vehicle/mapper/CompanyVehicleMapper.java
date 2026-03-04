package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.model.CompanyVehicle;
import org.springframework.stereotype.Component;

@Component
public class CompanyVehicleMapper {
    public CompanyVehicleResponse toResponse(CompanyVehicle m) {
        return new CompanyVehicleResponse(
                m.companyVehicleId(),
                m.companyId(),
                m.companyCode(),
                m.companyProject(),
                m.companyBranch(),
                m.companyDepartment(),
                m.companyVehicleType(),
                m.companyVehicleModel(),
                m.companyVehicleModelName(),
                m.categoryId(),
                m.companyVehicleManufacture(),
                m.companyVehicleManufactureBrand(),
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
                m.currentOwnership(),
                m.previousOwnersCount(),
                m.manufactureId(),
                m.distributorId(),
                m.vehicleCondition(),
                m.operationalStatusId(),
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
