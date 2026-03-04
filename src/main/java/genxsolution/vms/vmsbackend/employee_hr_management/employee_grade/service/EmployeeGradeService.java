package genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.service;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.dto.EmployeeGradeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.dto.EmployeeGradeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.mapper.EmployeeGradeMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_grade.repository.EmployeeGradeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeGradeService {

    private final EmployeeGradeRepository repository;
    private final EmployeeGradeMapper mapper;

    public EmployeeGradeService(EmployeeGradeRepository repository, EmployeeGradeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EmployeeGradeResponse> list(UUID companyId) {
        return repository.findAll(companyId).stream().map(mapper::toResponse).toList();
    }

    public EmployeeGradeResponse getById(UUID gradeId) {
        return repository.findById(gradeId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeGrade", gradeId.toString()));
    }

    public EmployeeGradeResponse create(EmployeeGradeUpsertRequest request) {
        return mapper.toResponse(repository.create(request));
    }

    public EmployeeGradeResponse update(UUID gradeId, EmployeeGradeUpsertRequest request) {
        return repository.update(gradeId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeGrade", gradeId.toString()));
    }

    public void delete(UUID gradeId) {
        if (!repository.delete(gradeId)) {
            throw new HrResourceNotFoundException("EmployeeGrade", gradeId.toString());
        }
    }
}







