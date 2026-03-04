package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HiredVehicleFitnessCertificateResponse(
        UUID fitnessId,
        UUID vehicleId,
        UUID supplierId,
        UUID hiredVehicleId,
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
        String HiredVehicleKeyNumber,
        String companyHiredVehicleRegistrationNumber,
        String HiredVehicleChassisNumber
) {}



