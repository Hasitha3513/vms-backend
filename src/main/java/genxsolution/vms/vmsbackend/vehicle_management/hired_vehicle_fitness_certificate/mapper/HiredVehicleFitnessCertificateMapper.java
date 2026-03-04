package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificateResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.model.HiredVehicleFitnessCertificate;
import org.springframework.stereotype.Component;

@Component
public class HiredVehicleFitnessCertificateMapper {
    public HiredVehicleFitnessCertificateResponse toResponse(HiredVehicleFitnessCertificate m) {
        return new HiredVehicleFitnessCertificateResponse(
                m.fitnessId(),
                m.vehicleId(),
                m.supplierId(),
                m.hiredVehicleId(),
                m.certificateNumber(),
                m.issuingAuthority(),
                m.inspectionCenter(),
                m.inspectorId(),
                m.inspectorName(),
                m.issueDate(),
                m.expiryDate(),
                m.validityDurationYears(),
                m.inspectionResultId(),
                m.remarks(),
                m.renewalReminderDays(),
                m.fitnessStatus(),
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



