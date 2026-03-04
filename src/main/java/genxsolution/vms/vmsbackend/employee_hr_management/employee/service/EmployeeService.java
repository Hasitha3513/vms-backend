package genxsolution.vms.vmsbackend.employee_hr_management.employee.service;

import genxsolution.vms.vmsbackend.employee_hr_management.employee.dto.EmployeeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.dto.EmployeeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.mapper.EmployeeMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.employee.repository.EmployeeRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    public EmployeeService(EmployeeRepository repository, EmployeeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<EmployeeResponse> list(UUID companyId) {
        return repository.findAll(companyId).stream().map(mapper::toResponse).toList();
    }

    public EmployeeResponse getById(UUID employeeId) {
        return repository.findById(employeeId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new HrResourceNotFoundException("Employee", employeeId.toString()));
    }

    public EmployeeResponse create(EmployeeUpsertRequest request) {
        validateCreateRequiredFields(request);
        validateUniqueWorkEmail(request.workEmail(), null);
        try {
            return mapper.toResponse(repository.create(request));
        } catch (DuplicateKeyException ex) {
            throw new IllegalArgumentException("Work email already exists");
        }
    }

    public EmployeeResponse update(UUID employeeId, EmployeeUpsertRequest request) {
        validateUniqueWorkEmail(request.workEmail(), employeeId);
        try {
            return repository.update(employeeId, request)
                    .map(mapper::toResponse)
                    .orElseThrow(() -> new HrResourceNotFoundException("Employee", employeeId.toString()));
        } catch (DuplicateKeyException ex) {
            throw new IllegalArgumentException("Work email already exists");
        }
    }

    public void delete(UUID employeeId) {
        if (!repository.delete(employeeId)) {
            throw new HrResourceNotFoundException("Employee", employeeId.toString());
        }
    }

    private void validateUniqueWorkEmail(String workEmail, UUID employeeId) {
        String normalized = normalize(workEmail);
        if (normalized == null) {
            return;
        }
        boolean exists = employeeId == null
                ? repository.existsByWorkEmail(normalized)
                : repository.existsByWorkEmailExcludingEmployeeId(normalized, employeeId);
        if (exists) {
            throw new IllegalArgumentException("Work email already exists");
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateCreateRequiredFields(EmployeeUpsertRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Employee payload is required");
        }
        if (request.companyId() == null) {
            throw new IllegalArgumentException("Company is required");
        }
        if (isBlank(request.companyCode())) {
            throw new IllegalArgumentException("Company code is required");
        }
        if (isBlank(request.employeeCode())) {
            throw new IllegalArgumentException("Employee code is required");
        }
        if (isBlank(request.firstName())) {
            throw new IllegalArgumentException("First name is required");
        }
        if (isBlank(request.lastName())) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (request.dateOfBirth() == null) {
            throw new IllegalArgumentException("Date of birth is required");
        }
        if (isBlank(request.nationalId())) {
            throw new IllegalArgumentException("National ID is required");
        }
        if (isBlank(request.mobilePhone())) {
            throw new IllegalArgumentException("Mobile phone is required");
        }
        if (request.hireDate() == null) {
            throw new IllegalArgumentException("Hire date is required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}







