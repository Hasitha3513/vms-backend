package genxsolution.vms.vmsbackend.maintenance_management.supplier.dto;

import java.util.UUID;

public record SupplierUpsertRequest(
        UUID companyId,
        String companyCode,
        String supplierName,
        String contactName,
        String contactPerson,
        String phone,
        String email,
        String address,
        String taxId,
        Integer supplierTypeId,
        Boolean isActive
) {
}
