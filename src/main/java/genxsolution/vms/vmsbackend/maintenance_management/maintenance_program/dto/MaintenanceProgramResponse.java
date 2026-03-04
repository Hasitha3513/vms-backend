package genxsolution.vms.vmsbackend.maintenance_management.maintenance_program.dto;
import java.util.UUID; import java.time.Instant;
public record MaintenanceProgramResponse(UUID programId, UUID companyId, String companyCode, String programName, Integer programTypeId, String description, Boolean isActive, Instant createdAt) {}
