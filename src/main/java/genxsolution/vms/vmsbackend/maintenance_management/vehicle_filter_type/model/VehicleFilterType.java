package genxsolution.vms.vmsbackend.maintenance_management.vehicle_filter_type.model;
import java.util.UUID;
public record VehicleFilterType(UUID filterTypeId, UUID companyId, String companyCode, String filterName, String filterCode, String description, Integer typicalLifeKm, Integer typicalLifeHours, Integer typicalLifeMonths) {}
