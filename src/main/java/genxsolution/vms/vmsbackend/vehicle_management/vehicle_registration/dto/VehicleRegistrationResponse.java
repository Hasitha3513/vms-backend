package genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record VehicleRegistrationResponse(
        UUID registrationId,
        UUID vehicleId,
        UUID companyId,
        UUID companyVehicleId,
        String registrationNumber,
        LocalDate registrationDate,
        LocalDate registrationExpiry,
        String registeringAuthority,
        String registrationState,
        String registrationCity,
        String rcBookNumber,
        String rcStatus,
        Integer numberPlateTypeId,
        Integer renewalReminderDays,
        String notes,
        Boolean isCurrent,
        Instant createdAt,
        Instant updatedAt,
        String companyName,
        String companyVehicleKeyNumber,
        String companyVehicleRegistrationNumber,
        String companyVehicleChassisNumber
) {}
