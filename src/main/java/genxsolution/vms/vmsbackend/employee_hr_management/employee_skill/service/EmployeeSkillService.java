package genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.service;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.dto.EmployeeSkillUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.dto.EmployeeSkillResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.mapper.EmployeeSkillMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_skill.repository.EmployeeSkillRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import java.util.Map;

@Service
public class EmployeeSkillService {

    private final EmployeeSkillRepository repository;
    private final EmployeeSkillMapper mapper;

    public EmployeeSkillService(EmployeeSkillRepository repository, EmployeeSkillMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EmployeeSkillResponse> list(UUID companyId, String q, int limit, Map<String, String> filters) {
        return repository.findAll(companyId, q, limit, filters).stream().map(mapper::toResponse).toList();
    }

    public EmployeeSkillResponse getById(UUID skillId) {
        return repository.findById(skillId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeSkill", skillId.toString()));
    }

    public EmployeeSkillResponse create(EmployeeSkillUpsertRequest request) {
        return mapper.toResponse(repository.create(request));
    }

    public EmployeeSkillResponse update(UUID skillId, EmployeeSkillUpsertRequest request) {
        return repository.update(skillId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeSkill", skillId.toString()));
    }

    public void delete(UUID skillId) {
        if (!repository.delete(skillId)) {
            throw new HrResourceNotFoundException("EmployeeSkill", skillId.toString());
        }
    }
}






