package genxsolution.vms.vmsbackend.vehicle_management.vehicle_operating_cost.dto;
import java.util.UUID; import java.time.LocalDate; import java.math.BigDecimal;
public record VehicleOperatingCostUpsertRequest(UUID companyId, String companyCode, UUID vehicleId, LocalDate costDate, Integer costTypeId, String description, BigDecimal amount, BigDecimal odometerKm, String referenceType, UUID referenceId) {}
