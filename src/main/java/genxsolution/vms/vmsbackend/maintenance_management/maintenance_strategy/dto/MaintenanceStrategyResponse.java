package genxsolution.vms.vmsbackend.maintenance_management.maintenance_strategy.dto;
import java.util.UUID;
public record MaintenanceStrategyResponse(UUID strategyId, UUID companyId, String companyCode, String strategyName, Integer strategyTypeId, String description) {}
