package genxsolution.vms.vmsbackend.maintenance_management.maintenance_assignment.dto;
import java.util.UUID; import java.time.Instant;
public record MaintenanceAssignmentUpsertRequest(UUID companyId, String companyCode, UUID breakdownId, UUID maintenanceId, UUID technicianId, Instant assignedAt, Instant startedAt, Instant completedAt, Integer statusId, String notes) {}
