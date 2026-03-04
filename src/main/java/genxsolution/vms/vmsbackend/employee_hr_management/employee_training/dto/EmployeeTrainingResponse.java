package genxsolution.vms.vmsbackend.employee_hr_management.employee_training.dto;

import java.time.Instant;
import java.util.UUID;

public record EmployeeTrainingResponse(
        UUID trainingId,
        UUID companyId,
        String companyCode,
        String trainingName,
        Integer trainingTypeId,
        String description,
        Integer durationHours,
        String provider,
        Instant createdAt
) {
}







