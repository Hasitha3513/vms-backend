package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto;

import java.time.LocalDate;
import java.util.UUID;

public record HiredVehicleRegistrationPrefillResponse(
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
        Boolean isCurrent
) {}




