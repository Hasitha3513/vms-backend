package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.model;

import java.time.Instant;
import java.util.UUID;

public record CompanyVehicleIdType(
        UUID idTypeId,
        UUID companyId,
        String companyName,
        String companyCode,
        UUID typeId,
        String typeName,
        String idTypeCode,
        Boolean isActive,
        Instant createdAt,
        Instant updatedAt
) {}
