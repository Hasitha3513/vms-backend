package genxsolution.vms.vmsbackend.maintenance_management.vehicle_maintenance_program_assignment.dto;
import java.util.UUID; import java.time.LocalDate; import java.math.BigDecimal;
public record VehicleMaintenanceProgramAssignmentUpsertRequest(UUID companyId, String companyCode, UUID vehicleId, UUID templateId, LocalDate startDate, LocalDate endDate, BigDecimal currentOdometer, BigDecimal currentEngineHours, LocalDate nextServiceDate, BigDecimal nextServiceOdometer, BigDecimal nextServiceHours, Boolean isActive) {}
