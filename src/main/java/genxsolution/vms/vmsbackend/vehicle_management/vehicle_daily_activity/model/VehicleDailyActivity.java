package genxsolution.vms.vmsbackend.vehicle_management.vehicle_daily_activity.model;
import java.util.UUID; import java.time.Instant; import java.time.LocalDate; import java.math.BigDecimal;
public record VehicleDailyActivity(UUID activityId, UUID companyId, String companyCode, UUID vehicleId, UUID driverId, UUID projectId, LocalDate activityDate, Instant startTime, Instant endTime, BigDecimal startOdometerKm, BigDecimal endOdometerKm, BigDecimal engineHours, BigDecimal distanceKm, String workDescription, String remarks, Instant createdAt) {}
