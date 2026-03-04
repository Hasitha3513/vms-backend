package genxsolution.vms.vmsbackend.vehicle_management.vehicle_report.dto;

import java.time.LocalDate;
import java.util.UUID;

public record VehicleLicenseReportResponse(
        UUID registrationId,
        String companyName,
        String companyVehicleKeyNumber,
        String companyVehicleRegistrationNumber,
        LocalDate registrationDate,
        LocalDate registrationExpiry,
        String registrationCity,
        String rcStatus,
        Long daysRemaining
) {}
