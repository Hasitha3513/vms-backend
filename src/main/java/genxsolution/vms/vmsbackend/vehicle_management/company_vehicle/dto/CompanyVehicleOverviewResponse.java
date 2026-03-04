package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto;

import java.util.List;

public record CompanyVehicleOverviewResponse(
        long totalVehicles,
        long activeVehicles,
        long inactiveVehicles,
        long totalTypes,
        long totalBrands,
        long totalInsuranceRecords,
        long totalFitnessRecords,
        long totalEmissionRecords,
        List<CompanyVehicleTypeCountResponse> vehicleTypeCounts
) {}
