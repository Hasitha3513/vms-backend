package genxsolution.vms.vmsbackend.employee_hr_management.employee.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeeResponse(
        UUID employeeId,
        UUID companyId,
        String companyCode,
        UUID branchId,
        UUID departmentId,
        UUID gradeId,
        UUID positionId,
        UUID managerId,
        String employeeCode,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        Integer genderId,
        String nationalId,
        String mobilePhone,
        String workEmail,
        String currentAddress,
        LocalDate hireDate,
        Integer employmentTypeId,
        String jobTitle,
        Boolean isDriver,
        Boolean isOperator,
        Boolean isTechnician,
        Integer employmentStatusId,
        Instant createdAt,
        Instant updatedAt
) {
}







