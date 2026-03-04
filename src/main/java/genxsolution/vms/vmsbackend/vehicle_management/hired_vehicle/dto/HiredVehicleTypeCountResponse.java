package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto;

import java.util.UUID;

public record HiredVehicleTypeCountResponse(
        UUID typeId,
        String typeName,
        long vehicleCount
) {}
