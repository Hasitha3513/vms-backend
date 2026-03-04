package genxsolution.vms.vmsbackend.organization.service;

import genxsolution.vms.vmsbackend.organization.dto.jobposition.CreateJobPositionRequest;
import genxsolution.vms.vmsbackend.organization.dto.jobposition.JobPositionResponse;
import genxsolution.vms.vmsbackend.organization.dto.jobposition.UpdateJobPositionRequest;
import genxsolution.vms.vmsbackend.organization.exception.ResourceNotFoundException;
import genxsolution.vms.vmsbackend.organization.mapper.JobPositionMapper;
import genxsolution.vms.vmsbackend.organization.repository.JobPositionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class JobPositionService {

    private final JobPositionRepository repository;
    private final JobPositionMapper mapper;

    public JobPositionService(JobPositionRepository repository, JobPositionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<JobPositionResponse> list(UUID companyId, boolean activeOnly) {
        if (companyId == null) {
            return repository.findAll(activeOnly).stream().map(mapper::toResponse).toList();
        }
        return repository.findAllByCompany(companyId, activeOnly).stream().map(mapper::toResponse).toList();
    }

    public JobPositionResponse getById(UUID positionId) {
        return repository.findById(positionId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosition", positionId.toString()));
    }

    public JobPositionResponse create(CreateJobPositionRequest request) {
        return mapper.toResponse(repository.create(request));
    }

    public JobPositionResponse update(UUID positionId, UpdateJobPositionRequest request) {
        return repository.update(positionId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("JobPosition", positionId.toString()));
    }

    public void delete(UUID positionId) {
        if (!repository.delete(positionId)) {
            throw new ResourceNotFoundException("JobPosition", positionId.toString());
        }
    }
}






