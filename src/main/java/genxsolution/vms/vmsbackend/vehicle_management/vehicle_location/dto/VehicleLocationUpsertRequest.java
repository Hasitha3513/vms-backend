package genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.dto;

import java.time.LocalDate;
import java.util.UUID;

public record VehicleLocationUpsertRequest(
        UUID vehicleId,
        UUID companyId,
        UUID branchId,
        UUID departmentId,
        String locationType,
        String locationName,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String country,
        String pinCode,
        String assignedZone,
        String assignedRegion,
        LocalDate periodStartDate,
        LocalDate periodEndDate,
        UUID recordedBy,
        Boolean isCurrent,
        String notes
) {}
