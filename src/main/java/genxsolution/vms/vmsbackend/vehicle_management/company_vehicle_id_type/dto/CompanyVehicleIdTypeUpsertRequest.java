package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto;

import java.util.UUID;

public record CompanyVehicleIdTypeUpsertRequest(
        UUID companyId,
        String companyCode,
        UUID typeId,
        String idTypeCode,
        Boolean isActive
) {}
