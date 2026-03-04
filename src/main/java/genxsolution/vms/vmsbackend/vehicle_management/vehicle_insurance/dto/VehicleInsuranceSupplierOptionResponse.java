package genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto;

import java.util.UUID;

public record VehicleInsuranceSupplierOptionResponse(
        UUID supplierId,
        String supplierCode,
        String supplierName
) {
}
