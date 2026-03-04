package genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record VehicleFitnessCertificate(
        UUID fitnessId,
        UUID vehicleId,
        UUID companyId,
        UUID companyVehicleId,
        String certificateNumber,
        String issuingAuthority,
        String inspectionCenter,
        String inspectorId,
        String inspectorName,
        LocalDate issueDate,
        LocalDate expiryDate,
        Integer validityDurationYears,
        Integer inspectionResultId,
        String remarks,
        Integer renewalReminderDays,
        String fitnessStatus,
        Boolean isCurrent,
        Instant createdAt,
        Instant updatedAt,
        String companyName,
        String companyVehicleKeyNumber,
        String companyVehicleRegistrationNumber,
        String companyVehicleChassisNumber
) {}
