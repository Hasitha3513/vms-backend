package genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificateResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.model.VehicleFitnessCertificate;
import org.springframework.stereotype.Component;

@Component
public class VehicleFitnessCertificateMapper {
    public VehicleFitnessCertificateResponse toResponse(VehicleFitnessCertificate m) {
        return new VehicleFitnessCertificateResponse(
                m.fitnessId(),
                m.vehicleId(),
                m.companyId(),
                m.companyVehicleId(),
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
                m.companyVehicleKeyNumber(),
                m.companyVehicleRegistrationNumber(),
                m.companyVehicleChassisNumber()
        );
    }
}
