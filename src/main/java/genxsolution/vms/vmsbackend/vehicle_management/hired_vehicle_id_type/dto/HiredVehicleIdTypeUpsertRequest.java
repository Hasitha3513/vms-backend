package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto;

import java.util.UUID;

public record HiredVehicleIdTypeUpsertRequest(
        UUID companyId,
        String companyCode,
        UUID typeId,
        String idTypeCode,
        Boolean isActive
) {}
