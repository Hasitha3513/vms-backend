package genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.model;

import java.util.UUID;

public record EmployeeSkill(
        UUID skillId,
        UUID companyId,
        String companyCode,
        String skillName,
        Integer skillCategoryId,
        String description
) {
}







