package genxsolution.vms.vmsbackend.maintenance_management.vehicle_maintenance_program.model;
import java.util.UUID; import java.time.LocalDate; import java.time.Instant;
public record VehicleMaintenanceProgram(UUID vehicleProgramId, UUID companyId, String companyCode, UUID vehicleId, UUID programId, LocalDate startDate, LocalDate endDate, Boolean isActive, Instant createdAt) {}
