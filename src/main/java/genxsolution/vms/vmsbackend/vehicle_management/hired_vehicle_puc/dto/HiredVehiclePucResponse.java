package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HiredVehiclePucResponse(
        UUID pucId,
        UUID vehicleId,
        UUID supplierId,
        UUID hiredVehicleId,
        String certificateNumber,
        String issuingCenter,
        LocalDate issueDate,
        LocalDate expiryDate,
        BigDecimal coEmissionPercent,
        BigDecimal hcEmissionPpm,
        String testResult,
        String pucStatus,
        Integer renewalReminderDays,
        Boolean isCurrent,
        Instant createdAt,
        Instant updatedAt,
        String companyName,
        String HiredVehicleKeyNumber,
        String companyHiredVehicleRegistrationNumber,
        String HiredVehicleChassisNumber
) {}



