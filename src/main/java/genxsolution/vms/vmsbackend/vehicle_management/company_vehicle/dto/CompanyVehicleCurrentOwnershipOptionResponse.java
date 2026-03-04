package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto;

import java.util.UUID;

public record CompanyVehicleCurrentOwnershipOptionResponse(
        UUID ownerId,
        String ownerCode,
        String ownerName
) {}
