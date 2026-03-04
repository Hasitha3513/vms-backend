package genxsolution.vms.vmsbackend.maintenance_management.supplier.dto;

public record SupplierTypeCountResponse(
        Integer supplierTypeId,
        String supplierTypeName,
        Long supplierCount
) {
}
