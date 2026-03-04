package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto.CompanyVehicleIdTypeResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.model.CompanyVehicleIdType;
import org.springframework.stereotype.Component;

@Component
public class CompanyVehicleIdTypeMapper {
    public CompanyVehicleIdTypeResponse toResponse(CompanyVehicleIdType m) {
        return new CompanyVehicleIdTypeResponse(
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
