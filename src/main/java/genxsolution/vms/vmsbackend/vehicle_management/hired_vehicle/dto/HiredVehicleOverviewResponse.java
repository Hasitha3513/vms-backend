package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto;

import java.util.List;

public record HiredVehicleOverviewResponse(
        long totalVehicles,
        long activeVehicles,
        long inactiveVehicles,
        long totalTypes,
        long totalBrands,
        List<HiredVehicleTypeCountResponse> vehicleTypeCounts
) {}
