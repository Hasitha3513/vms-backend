package genxsolution.vms.vmsbackend.maintenance_management.supplier.dto;

import java.time.Instant;
import java.util.UUID;

public record SupplierDetailsResponse(
        UUID supplierId,
        String supplierCode,
        String supplierName,
        String contactName,
        String contactPerson,
        String phone,
        String email,
        String address,
        String taxId,
        Integer supplierTypeId,
        String supplierTypeName,
        Boolean isActive,
        Instant createdAt
) {
}
