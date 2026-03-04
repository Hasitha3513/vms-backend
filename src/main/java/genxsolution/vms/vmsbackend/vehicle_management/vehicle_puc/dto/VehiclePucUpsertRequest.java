package genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record VehiclePucUpsertRequest(
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
        Boolean isCurrent
) {}
