package genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.model.VehicleRegistration;
import org.springframework.stereotype.Component;

@Component
public class VehicleRegistrationMapper {
    public VehicleRegistrationResponse toResponse(VehicleRegistration m) {
        return new VehicleRegistrationResponse(
                m.registrationId(),
                m.vehicleId(),
                m.companyId(),
                m.companyVehicleId(),
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
                m.companyVehicleKeyNumber(),
                m.companyVehicleRegistrationNumber(),
                m.companyVehicleChassisNumber()
        );
    }
}
