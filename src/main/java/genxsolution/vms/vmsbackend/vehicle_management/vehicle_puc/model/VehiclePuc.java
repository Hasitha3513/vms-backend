package genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record VehiclePuc(
        UUID pucId,
        UUID vehicleId,
        UUID companyId,
        UUID companyVehicleId,
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
        String companyVehicleKeyNumber,
        String companyVehicleRegistrationNumber,
        String companyVehicleChassisNumber
) {}
