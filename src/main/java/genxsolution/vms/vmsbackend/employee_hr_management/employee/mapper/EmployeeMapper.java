package genxsolution.vms.vmsbackend.employee_hr_management.employee.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.employee.dto.EmployeeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.model.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {
    public EmployeeResponse toResponse(Employee m) {
        return new EmployeeResponse(m.employeeId(), m.companyId(), m.companyCode(), m.branchId(), m.departmentId(), m.gradeId(),
                m.positionId(), m.managerId(), m.employeeCode(), m.firstName(), m.lastName(), m.dateOfBirth(), m.genderId(),
                m.nationalId(), m.mobilePhone(), m.workEmail(), m.currentAddress(), m.hireDate(),
                m.employmentTypeId(), m.jobTitle(), m.isDriver(), m.isOperator(), m.isTechnician(), m.employmentStatusId(),
                m.createdAt(), m.updatedAt());
    }
}







