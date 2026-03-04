package genxsolution.vms.vmsbackend.maintenance_management.maintenance_strategy.model;
import java.util.UUID;
public record MaintenanceStrategy(UUID strategyId, UUID companyId, String companyCode, String strategyName, Integer strategyTypeId, String description) {}
