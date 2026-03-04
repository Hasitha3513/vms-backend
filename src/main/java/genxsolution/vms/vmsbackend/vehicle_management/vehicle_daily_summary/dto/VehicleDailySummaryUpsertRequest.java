package genxsolution.vms.vmsbackend.vehicle_management.vehicle_daily_summary.dto;
import java.util.UUID; import java.time.LocalDate; import java.math.BigDecimal;
public record VehicleDailySummaryUpsertRequest(UUID companyId, String companyCode, UUID vehicleId, LocalDate summaryDate, BigDecimal totalDistance, BigDecimal totalEngineHours, BigDecimal totalFuelConsumed, BigDecimal avgFuelEfficiency, Integer totalTrips, BigDecimal operationalHours, BigDecimal idleHours, BigDecimal maintenanceHours) {}
