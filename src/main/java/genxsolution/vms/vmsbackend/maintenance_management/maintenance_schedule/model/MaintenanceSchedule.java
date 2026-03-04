package genxsolution.vms.vmsbackend.maintenance_management.maintenance_schedule.model;
import java.util.UUID; import java.time.LocalDate; import java.math.BigDecimal; import java.time.Instant;
public record MaintenanceSchedule(UUID scheduleId, UUID companyId, String companyCode, UUID vehicleId, UUID standardId, LocalDate scheduledDate, BigDecimal scheduledOdometerKm, BigDecimal scheduledEngineHours, LocalDate aiPredictedDate, BigDecimal predictionConfidence, Integer statusId, Boolean notificationSent, Instant notificationSentDate, Integer reminderCount, Instant createdAt, UUID createdBy) {}
