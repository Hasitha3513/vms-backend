package genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record VehicleInsuranceUpsertRequest(
        UUID vehicleId,
        UUID companyId,
        UUID companyVehicleId,
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
