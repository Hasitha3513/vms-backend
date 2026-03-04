package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.model;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HiredVehicleRegistration(
        UUID registrationId,
        UUID vehicleId,
        UUID supplierId,
        UUID hiredVehicleId,
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
        String HiredVehicleKeyNumber,
        String companyHiredVehicleRegistrationNumber,
        String HiredVehicleChassisNumber
) {}



