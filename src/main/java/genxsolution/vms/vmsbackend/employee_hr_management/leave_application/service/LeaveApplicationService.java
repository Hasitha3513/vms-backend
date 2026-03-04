package genxsolution.vms.vmsbackend.employee_hr_management.leave_application.service;

import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto.LeaveApplicationResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto.LeaveApplicationUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.mapper.LeaveApplicationMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.repository.LeaveApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeaveApplicationService {
    private final LeaveApplicationRepository repository; private final LeaveApplicationMapper mapper;
    public LeaveApplicationService(LeaveApplicationRepository repository, LeaveApplicationMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<LeaveApplicationResponse> list(UUID companyId) { return repository.findAll(companyId).stream().map(mapper::toResponse).toList(); }
    public LeaveApplicationResponse getById(UUID leaveId) { return repository.findById(leaveId).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("LeaveApplication", leaveId.toString())); }
    public LeaveApplicationResponse create(LeaveApplicationUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public LeaveApplicationResponse update(UUID leaveId, LeaveApplicationUpsertRequest request) { return repository.update(leaveId, request).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("LeaveApplication", leaveId.toString())); }
    public void delete(UUID leaveId) { if (!repository.delete(leaveId)) throw new HrResourceNotFoundException("LeaveApplication", leaveId.toString()); }
}
