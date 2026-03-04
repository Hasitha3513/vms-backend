package genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.model.VehiclePuc;
import org.springframework.stereotype.Component;

@Component
public class VehiclePucMapper {
    public VehiclePucResponse toResponse(VehiclePuc m) {
        return new VehiclePucResponse(
                m.pucId(),
                m.vehicleId(),
                m.companyId(),
                m.companyVehicleId(),
                m.certificateNumber(),
                m.issuingCenter(),
                m.issueDate(),
                m.expiryDate(),
                m.coEmissionPercent(),
                m.hcEmissionPpm(),
                m.testResult(),
                m.pucStatus(),
                m.renewalReminderDays(),
                m.isCurrent(),
                m.createdAt(),
                m.updatedAt(),
                m.companyName(),
                m.companyVehicleKeyNumber(),
                m.companyVehicleRegistrationNumber(),
                m.companyVehicleChassisNumber()
        );
    }
}
