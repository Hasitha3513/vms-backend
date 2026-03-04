package genxsolution.vms.vmsbackend.organization.service;

import genxsolution.vms.vmsbackend.organization.dto.department.CreateDepartmentRequest;
import genxsolution.vms.vmsbackend.organization.dto.department.DepartmentResponse;
import genxsolution.vms.vmsbackend.organization.dto.department.UpdateDepartmentRequest;
import genxsolution.vms.vmsbackend.organization.exception.ResourceNotFoundException;
import genxsolution.vms.vmsbackend.organization.mapper.DepartmentMapper;
import genxsolution.vms.vmsbackend.organization.repository.CompanyRepository;
import genxsolution.vms.vmsbackend.organization.repository.DepartmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DepartmentService {

    private final DepartmentRepository repository;
    private final CompanyRepository companyRepository;
    private final DepartmentMapper mapper;

    public DepartmentService(DepartmentRepository repository, CompanyRepository companyRepository, DepartmentMapper mapper) {
        this.repository = repository;
        this.companyRepository = companyRepository;
        this.mapper = mapper;
    }

    public List<DepartmentResponse> list(UUID companyId, boolean activeOnly) {
        if (companyId == null) {
            return repository.findAll(activeOnly).stream().map(mapper::toResponse).toList();
        }
        return repository.findAllByCompany(companyId, activeOnly).stream().map(mapper::toResponse).toList();
    }

    public DepartmentResponse getById(UUID departmentId) {
        return repository.findById(departmentId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId.toString()));
    }

    public DepartmentResponse create(CreateDepartmentRequest request) {
        String companyCode = companyRepository.findById(request.companyId())
                .map(company -> company.companyCode())
                .orElseThrow(() -> new ResourceNotFoundException("Company", request.companyId().toString()));

        CreateDepartmentRequest resolved = new CreateDepartmentRequest(
                request.companyId(),
                companyCode,
                request.branchId(),
                request.departmentCode(),
                request.departmentName(),
                request.parentDepartmentId(),
                request.isActive()
        );

        return mapper.toResponse(repository.create(resolved));
    }

    public DepartmentResponse update(UUID departmentId, UpdateDepartmentRequest request) {
        return repository.update(departmentId, request)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Department", departmentId.toString()));
    }

    public void delete(UUID departmentId) {
        if (!repository.delete(departmentId)) {
            throw new ResourceNotFoundException("Department", departmentId.toString());
        }
    }
}






