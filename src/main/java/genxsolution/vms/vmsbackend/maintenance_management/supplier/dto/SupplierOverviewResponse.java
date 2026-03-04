package genxsolution.vms.vmsbackend.maintenance_management.supplier.dto;

import java.util.List;

public record SupplierOverviewResponse(
        long totalSuppliers,
        long activeSuppliers,
        long inactiveSuppliers,
        long totalTypes,
        List<SupplierTypeCountResponse> byType
) {
}
