package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto;

import java.util.UUID;

public record HiredVehicleSupplierOptionResponse(
        UUID supplierId,
        String supplierCode,
        String supplierName
) {}
