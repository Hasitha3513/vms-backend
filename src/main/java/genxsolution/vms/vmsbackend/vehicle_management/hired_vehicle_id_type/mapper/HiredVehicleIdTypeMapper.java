package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto.HiredVehicleIdTypeResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.model.HiredVehicleIdType;
import org.springframework.stereotype.Component;

@Component
public class HiredVehicleIdTypeMapper {
    public HiredVehicleIdTypeResponse toResponse(HiredVehicleIdType m) {
        return new HiredVehicleIdTypeResponse(
                m.idTypeId(),
                m.companyId(),
                m.companyName(),
                m.companyCode(),
                m.typeId(),
                m.typeName(),
                m.idTypeCode(),
                m.isActive(),
                m.createdAt(),
                m.updatedAt()
        );
    }
}
