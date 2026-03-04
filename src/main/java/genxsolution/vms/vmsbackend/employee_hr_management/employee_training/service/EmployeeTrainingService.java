package genxsolution.vms.vmsbackend.employee_hr_management.employee_training.service;

import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.dto.EmployeeTrainingUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.dto.EmployeeTrainingResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.mapper.EmployeeTrainingMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.employee_training.repository.EmployeeTrainingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeTrainingService {

    private final EmployeeTrainingRepository repository;
    private final EmployeeTrainingMapper mapper;

    public EmployeeTrainingService(EmployeeTrainingRepository repository, EmployeeTrainingMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EmployeeTrainingResponse> list(UUID companyId) {
        return repository.findAll(companyId).stream().map(mapper::toResponse).toList();
    }

    public EmployeeTrainingResponse getById(UUID trainingId) {
        return repository.findById(trainingId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeTraining", trainingId.toString()));
    }

    public EmployeeTrainingResponse create(EmployeeTrainingUpsertRequest request) {
        return mapper.toResponse(repository.create(request));
    }

    public EmployeeTrainingResponse update(UUID trainingId, EmployeeTrainingUpsertRequest request) {
        return repository.update(trainingId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new HrResourceNotFoundException("EmployeeTraining", trainingId.toString()));
    }

    public void delete(UUID trainingId) {
        if (!repository.delete(trainingId)) {
            throw new HrResourceNotFoundException("EmployeeTraining", trainingId.toString());
        }
    }
}







