package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.dto.VehicleModelResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model.model.VehicleModel;
import org.springframework.stereotype.Component;

@Component
public class VehicleModelMapper {
    public VehicleModelResponse toResponse(VehicleModel m) {
        return new VehicleModelResponse(
                m.modelId(),
                m.manufacturerCategoryId(),
                m.categoryId(),
                m.manufacturerId(),
                m.manufacturerCountry(),
                m.typeId(),
                m.modelName(),
                m.modelCode(),
                m.modelYear(),
                m.bodyStyleId(),
                m.fuelTypeId(),
                m.engineCapacityCc(),
                m.powerHp(),
                m.torqueNm(),
                m.numberOfCylinders(),
                m.transmissionTypeId(),
                m.drivetrainTypeId(),
                m.seatingCapacity(),
                m.numberOfDoors(),
                m.kerbWeightKg(),
                m.gvwKg(),
                m.wheelbaseMm(),
                m.fuelEfficiencyKmpl(),
                m.launchYear(),
                m.description(),
                m.imageUrl(),
                m.isActive(),
                m.createdAt(),
                m.updatedAt()
        );
    }
}
