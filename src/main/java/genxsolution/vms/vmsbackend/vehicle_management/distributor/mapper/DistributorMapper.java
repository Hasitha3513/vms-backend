package genxsolution.vms.vmsbackend.vehicle_management.distributor.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.distributor.dto.DistributorResponse;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.model.Distributor;
import org.springframework.stereotype.Component;

@Component
public class DistributorMapper {
    public DistributorResponse toResponse(Distributor m) {
        return new DistributorResponse(
                m.distributorId(),
                m.manufacturerId(),
                m.distributorName(),
                m.distributorCountry(),
                m.distributorLocation(),
                m.distributorLogo(),
                m.distributorPhoneNumber(),
                m.distributorEmail(),
                m.distributorDescription(),
                m.isActive(),
                m.createAt(),
                m.updateAt()
        );
    }
}
