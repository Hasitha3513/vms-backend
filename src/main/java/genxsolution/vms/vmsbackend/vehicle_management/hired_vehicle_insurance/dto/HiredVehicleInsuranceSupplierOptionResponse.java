package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto;

import java.util.UUID;

public record HiredVehicleInsuranceSupplierOptionResponse(
        UUID supplierId,
        String supplierCode,
        String supplierName
) {
}



