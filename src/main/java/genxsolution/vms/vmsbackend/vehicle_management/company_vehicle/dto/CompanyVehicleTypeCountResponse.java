package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto;

import java.util.UUID;

public record CompanyVehicleTypeCountResponse(
        UUID typeId,
        String typeName,
        long vehicleCount
) {}
