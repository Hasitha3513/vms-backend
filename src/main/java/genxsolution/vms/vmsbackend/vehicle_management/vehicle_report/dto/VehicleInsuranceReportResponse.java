package genxsolution.vms.vmsbackend.vehicle_management.vehicle_report.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record VehicleInsuranceReportResponse(
        UUID insuranceId,
        String companyName,
        String companyVehicleKeyNumber,
        String companyVehicleRegistrationNumber,
        String insuranceCompany,
        String policyNumber,
        LocalDate policyStartDate,
        LocalDate policyExpiryDate,
        BigDecimal premiumAmount,
        String insuranceStatus,
        Long daysRemaining
) {}

