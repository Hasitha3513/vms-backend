package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto;

import java.time.Instant;
import java.util.UUID;

public record CompanyVehicleIdTypeResponse(
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
