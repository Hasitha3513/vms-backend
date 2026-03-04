package genxsolution.vms.vmsbackend.vehicle_management.vehicle_daily_summary.dto;
import java.util.UUID; import java.time.Instant; import java.time.LocalDate; import java.math.BigDecimal;
public record VehicleDailySummaryResponse(UUID summaryId, UUID companyId, String companyCode, UUID vehicleId, LocalDate summaryDate, BigDecimal totalDistance, BigDecimal totalEngineHours, BigDecimal totalFuelConsumed, BigDecimal avgFuelEfficiency, Integer totalTrips, BigDecimal operationalHours, BigDecimal idleHours, BigDecimal maintenanceHours, Instant createdAt) {}
