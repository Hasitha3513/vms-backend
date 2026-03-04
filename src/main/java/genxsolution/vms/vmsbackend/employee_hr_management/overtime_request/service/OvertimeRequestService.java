package genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.service;

import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.dto.OvertimeRequestResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.dto.OvertimeRequestUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.mapper.OvertimeRequestMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.repository.OvertimeRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OvertimeRequestService {
    private final OvertimeRequestRepository repository; private final OvertimeRequestMapper mapper;
    public OvertimeRequestService(OvertimeRequestRepository repository, OvertimeRequestMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<OvertimeRequestResponse> list(UUID companyId) { return repository.findAll(companyId).stream().map(mapper::toResponse).toList(); }
    public OvertimeRequestResponse getById(UUID overtimeId) { return repository.findById(overtimeId).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("OvertimeRequest", overtimeId.toString())); }
    public OvertimeRequestResponse create(OvertimeRequestUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public OvertimeRequestResponse update(UUID overtimeId, OvertimeRequestUpsertRequest request) { return repository.update(overtimeId, request).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("OvertimeRequest", overtimeId.toString())); }
    public void delete(UUID overtimeId) { if (!repository.delete(overtimeId)) throw new HrResourceNotFoundException("OvertimeRequest", overtimeId.toString()); }
}
