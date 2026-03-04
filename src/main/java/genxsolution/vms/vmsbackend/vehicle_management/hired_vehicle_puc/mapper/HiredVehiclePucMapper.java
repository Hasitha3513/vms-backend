package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.model.HiredVehiclePuc;
import org.springframework.stereotype.Component;

@Component
public class HiredVehiclePucMapper {
    public HiredVehiclePucResponse toResponse(HiredVehiclePuc m) {
        return new HiredVehiclePucResponse(
                m.pucId(),
                m.vehicleId(),
                m.supplierId(),
                m.hiredVehicleId(),
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
                m.HiredVehicleKeyNumber(),
                m.companyHiredVehicleRegistrationNumber(),
                m.HiredVehicleChassisNumber()
        );
    }
}



