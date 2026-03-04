package genxsolution.vms.vmsbackend.maintenance_management.vehicle_maintenance_program.dto;
import java.util.UUID; import java.time.LocalDate;
public record VehicleMaintenanceProgramUpsertRequest(UUID companyId, String companyCode, UUID vehicleId, UUID programId, LocalDate startDate, LocalDate endDate, Boolean isActive) {}
