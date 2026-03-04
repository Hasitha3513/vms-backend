package genxsolution.vms.vmsbackend.maintenance_management.repair_job.model;
import java.util.UUID; import java.math.BigDecimal; import java.time.LocalDate; import java.time.Instant;
public record RepairJob(UUID repairJobId, UUID companyId, String companyCode, UUID breakdownId, Integer repairTypeId, String diagnosisNotes, String decidedSolution, BigDecimal estimatedCost, BigDecimal actualCost, LocalDate startDate, LocalDate completionDate, Integer statusId, UUID createdBy, Instant createdAt, Instant updatedAt) {}
