package genxsolution.vms.vmsbackend.maintenance_management.vehicle_filter_type.dto;
import java.util.UUID;
public record VehicleFilterTypeUpsertRequest(UUID companyId, String companyCode, String filterName, String filterCode, String description, Integer typicalLifeKm, Integer typicalLifeHours, Integer typicalLifeMonths) {}
