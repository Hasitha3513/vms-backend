package genxsolution.vms.vmsbackend.employee_hr_management.employee_training.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.dto.EmployeeTrainingResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.model.EmployeeTraining;
import org.springframework.stereotype.Component;

@Component
public class EmployeeTrainingMapper {
    public EmployeeTrainingResponse toResponse(EmployeeTraining m) {
        return new EmployeeTrainingResponse(m.trainingId(), m.companyId(), m.companyCode(), m.trainingName(),
                m.trainingTypeId(), m.description(), m.durationHours(), m.provider(), m.createdAt());
    }
}







