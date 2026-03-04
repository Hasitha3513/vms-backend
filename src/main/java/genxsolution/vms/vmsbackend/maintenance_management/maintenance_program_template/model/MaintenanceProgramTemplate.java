package genxsolution.vms.vmsbackend.maintenance_management.maintenance_program_template.model;
import java.util.UUID; import java.math.BigDecimal;
public record MaintenanceProgramTemplate(UUID templateId, UUID companyId, String companyCode, String programName, UUID vehicleTypeId, String programType, String intervalType, Integer intervalValue, String checklistTemplate, BigDecimal estimatedDurationHours, BigDecimal estimatedCost, Boolean isActive) {}
