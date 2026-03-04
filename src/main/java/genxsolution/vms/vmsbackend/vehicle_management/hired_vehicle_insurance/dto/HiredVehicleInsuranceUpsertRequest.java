package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record HiredVehicleInsuranceUpsertRequest(
        UUID vehicleId,
        UUID supplierId,
        UUID hiredVehicleId,
        String insuranceCompany,
        String policyNumber,
        Integer insuranceTypeId,
        LocalDate policyStartDate,
        LocalDate policyExpiryDate,
        BigDecimal idvAmount,
        BigDecimal premiumAmount,
        String paymentMode,
        LocalDate paymentDate,
        String agentName,
        String agentContact,
        String agentEmail,
        String nomineeName,
        String addOnCovers,
        BigDecimal ncbPercent,
        Integer claimCount,
        LocalDate lastClaimDate,
        BigDecimal lastClaimAmount,
        Integer renewalReminderDays,
        String insuranceStatus,
        String notes,
        Boolean isCurrent
) {}



