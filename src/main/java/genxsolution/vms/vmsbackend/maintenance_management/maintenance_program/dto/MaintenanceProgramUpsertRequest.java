package genxsolution.vms.vmsbackend.maintenance_management.maintenance_program.dto;
import java.util.UUID;
public record MaintenanceProgramUpsertRequest(UUID companyId, String companyCode, String programName, Integer programTypeId, String description, Boolean isActive) {}
