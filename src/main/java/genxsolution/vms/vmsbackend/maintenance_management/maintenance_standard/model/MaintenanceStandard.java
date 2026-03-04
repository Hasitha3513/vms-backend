package genxsolution.vms.vmsbackend.maintenance_management.maintenance_standard.model;
import java.util.UUID;
public record MaintenanceStandard(UUID standardId, UUID companyId, String companyCode, UUID typeId, UUID strategyId, String standardCode, String name, Integer categoryId, Integer intervalKm, Integer intervalMonths, Integer intervalEngineHours, String checklist, Boolean isActive) {}
