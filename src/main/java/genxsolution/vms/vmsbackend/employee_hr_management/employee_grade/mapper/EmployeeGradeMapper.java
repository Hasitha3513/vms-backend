package genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.dto.EmployeeGradeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.model.EmployeeGrade;
import org.springframework.stereotype.Component;

@Component
public class EmployeeGradeMapper {
    public EmployeeGradeResponse toResponse(EmployeeGrade m) {
        return new EmployeeGradeResponse(m.gradeId(), m.companyId(), m.companyCode(), m.gradeCode(), m.gradeName(), m.categoryId(),
                m.baseSalary(), m.baseAllowance(), m.dailyAllowance(), m.overtimeRatePerHour(), m.notes(), m.createdAt());
    }
}







