package genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.dto.VehicleLocationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.model.VehicleLocation;
import org.springframework.stereotype.Component;

@Component
public class VehicleLocationMapper {
    public VehicleLocationResponse toResponse(VehicleLocation m) {
        return new VehicleLocationResponse(
                m.locationId(),
                m.vehicleId(),
                m.companyId(),
                m.branchId(),
                m.departmentId(),
                m.locationType(),
                m.locationName(),
                m.addressLine1(),
                m.addressLine2(),
                m.city(),
                m.state(),
                m.country(),
                m.pinCode(),
                m.assignedZone(),
                m.assignedRegion(),
                m.periodStartDate(),
                m.periodEndDate(),
                m.durationDays(),
                m.recordedAt(),
                m.recordedBy(),
                m.isCurrent(),
                m.notes()
        );
    }
}
