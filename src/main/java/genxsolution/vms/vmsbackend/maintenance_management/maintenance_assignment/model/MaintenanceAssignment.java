package genxsolution.vms.vmsbackend.maintenance_management.maintenance_assignment.model;
import java.util.UUID; import java.time.Instant;
public record MaintenanceAssignment(UUID assignmentId, UUID companyId, String companyCode, UUID breakdownId, UUID maintenanceId, UUID technicianId, Instant assignedAt, Instant startedAt, Instant completedAt, Integer statusId, String notes) {}
