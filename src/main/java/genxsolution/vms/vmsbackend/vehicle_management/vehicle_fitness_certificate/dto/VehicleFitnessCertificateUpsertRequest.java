package genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto;

import java.time.LocalDate;
import java.util.UUID;

public record VehicleFitnessCertificateUpsertRequest(
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
        Boolean isCurrent
) {}
