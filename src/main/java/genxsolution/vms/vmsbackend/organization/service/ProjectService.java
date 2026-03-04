package genxsolution.vms.vmsbackend.organization.service;

import genxsolution.vms.vmsbackend.organization.dto.project.CreateProjectRequest;
import genxsolution.vms.vmsbackend.organization.dto.project.ProjectResponse;
import genxsolution.vms.vmsbackend.organization.dto.project.UpdateProjectRequest;
import genxsolution.vms.vmsbackend.organization.exception.ResourceNotFoundException;
import genxsolution.vms.vmsbackend.organization.mapper.ProjectMapper;
import genxsolution.vms.vmsbackend.organization.repository.CompanyRepository;
import genxsolution.vms.vmsbackend.organization.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository repository;
    private final CompanyRepository companyRepository;
    private final ProjectMapper mapper;

    public ProjectService(ProjectRepository repository, CompanyRepository companyRepository, ProjectMapper mapper) {
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
    }

    public List<ProjectResponse> list(UUID companyId) {
        if (companyId == null) {
            return repository.findAll().stream().map(mapper::toResponse).toList();
        }
        return repository.findAllByCompany(companyId).stream().map(mapper::toResponse).toList();
    }

    public ProjectResponse getById(UUID projectId) {
        return repository.findById(projectId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }

    public ProjectResponse create(CreateProjectRequest request) {
        String companyCode = companyRepository.findById(request.companyId())
                .map(company -> company.companyCode())
                .orElseThrow(() -> new ResourceNotFoundException("Company", request.companyId().toString()));

        CreateProjectRequest resolved = new CreateProjectRequest(
                request.companyId(),
                companyCode,
                request.branchId(),
                request.projectCode(),
                request.projectName(),
                request.projectTypeId(),
                request.siteAddress(),
                request.siteLatitude(),
                request.siteLongitude(),
                request.startDate(),
                request.plannedEndDate(),
                request.actualEndDate(),
                request.budgetAmount(),
                request.actualCost(),
                request.projectManager(),
                request.statusId()
        );
        return mapper.toResponse(repository.create(resolved));
    }

    public ProjectResponse update(UUID projectId, UpdateProjectRequest request) {
        return repository.update(projectId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Project", projectId.toString()));
    }

    public void delete(UUID projectId) {
        if (!repository.delete(projectId)) {
            throw new ResourceNotFoundException("Project", projectId.toString());
        }
    }
}






