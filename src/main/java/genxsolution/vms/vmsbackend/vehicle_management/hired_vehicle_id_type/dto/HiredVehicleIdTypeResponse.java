package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto;

import java.time.Instant;
import java.util.UUID;

public record HiredVehicleIdTypeResponse(
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
