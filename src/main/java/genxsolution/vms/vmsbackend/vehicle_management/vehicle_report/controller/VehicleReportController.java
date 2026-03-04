package genxsolution.vms.vmsbackend.vehicle_management.vehicle_report.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.service.VehicleInsuranceService;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.service.VehicleRegistrationService;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_report.dto.VehicleInsuranceReportResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_report.dto.VehicleLicenseReportResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/reports")
public class VehicleReportController {
    private final VehicleRegistrationService vehicleRegistrationService;
    private final VehicleInsuranceService vehicleInsuranceService;

    public VehicleReportController(
            VehicleRegistrationService vehicleRegistrationService,
            VehicleInsuranceService vehicleInsuranceService
    ) {
        this.vehicleRegistrationService = vehicleRegistrationService;
        this.vehicleInsuranceService = vehicleInsuranceService;
    }

    @GetMapping("/license")
    public List<VehicleLicenseReportResponse> vehicleLicenseReport(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID companyVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        List<VehicleLicenseReportResponse> rows = vehicleRegistrationService
                .list(companyId, companyVehicleId)
                .stream()
                .map(this::toLicenseReportRow)
                .toList();
        return ListQueryEngine.apply(rows, filters, Set.of("companyId", "companyVehicleId"));
    }

    @GetMapping("/insurance")
    public List<VehicleInsuranceReportResponse> vehicleInsuranceReport(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID companyVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        List<VehicleInsuranceReportResponse> rows = vehicleInsuranceService
                .list(companyId, companyVehicleId)
                .stream()
                .map(this::toInsuranceReportRow)
                .toList();
        return ListQueryEngine.apply(rows, filters, Set.of("companyId", "companyVehicleId"));
    }

    private VehicleLicenseReportResponse toLicenseReportRow(VehicleRegistrationResponse row) {
        return new VehicleLicenseReportResponse(
                row.registrationId(),
                row.companyName(),
                row.companyVehicleKeyNumber(),
                row.companyVehicleRegistrationNumber(),
                row.registrationDate(),
                row.registrationExpiry(),
                row.registrationCity(),
                row.rcStatus(),
                calculateDaysRemaining(row.registrationExpiry())
        );
    }

    private Long calculateDaysRemaining(LocalDate expiryDate) {
        if (expiryDate == null) return null;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    private VehicleInsuranceReportResponse toInsuranceReportRow(VehicleInsuranceResponse row) {
        return new VehicleInsuranceReportResponse(
                row.insuranceId(),
                row.companyName(),
                row.companyVehicleKeyNumber(),
                row.companyVehicleRegistrationNumber(),
                row.insuranceCompany(),
                row.policyNumber(),
                row.policyStartDate(),
                row.policyExpiryDate(),
                row.premiumAmount(),
                row.insuranceStatus(),
                calculateDaysRemaining(row.policyExpiryDate())
        );
    }
}
