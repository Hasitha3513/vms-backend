package genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.dto;

import java.util.UUID;

public record EmployeeSkillUpsertRequest(
        UUID companyId,
        String companyCode,
        String skillName,
        Integer skillCategoryId,
        String description
) {
}







