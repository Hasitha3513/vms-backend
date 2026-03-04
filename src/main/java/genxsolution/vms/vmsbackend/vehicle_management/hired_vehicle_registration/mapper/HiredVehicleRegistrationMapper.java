package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.model.HiredVehicleRegistration;
import org.springframework.stereotype.Component;

@Component
public class HiredVehicleRegistrationMapper {
    public HiredVehicleRegistrationResponse toResponse(HiredVehicleRegistration m) {
        return new HiredVehicleRegistrationResponse(
                m.registrationId(),
                m.vehicleId(),
                m.supplierId(),
                m.hiredVehicleId(),
                m.registrationNumber(),
                m.registrationDate(),
                m.registrationExpiry(),
                m.registeringAuthority(),
                m.registrationState(),
                m.registrationCity(),
                m.rcBookNumber(),
                m.rcStatus(),
                m.numberPlateTypeId(),
                m.renewalReminderDays(),
                m.notes(),
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



