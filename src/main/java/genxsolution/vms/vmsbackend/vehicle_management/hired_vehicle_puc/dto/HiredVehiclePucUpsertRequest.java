package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record HiredVehiclePucUpsertRequest(
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
        Boolean isCurrent
) {}



