package genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.dto.EmployeeSkillResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.model.EmployeeSkill;
import org.springframework.stereotype.Component;

@Component
public class EmployeeSkillMapper {
    public EmployeeSkillResponse toResponse(EmployeeSkill m) {
        return new EmployeeSkillResponse(m.skillId(), m.companyId(), m.companyCode(), m.skillName(), m.skillCategoryId(), m.description());
    }
}







