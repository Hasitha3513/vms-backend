package genxsolution.vms.vmsbackend.employee_hr_management.leave_type.service;

import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.dto.LeaveTypeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.dto.LeaveTypeUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.mapper.LeaveTypeMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.repository.LeaveTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LeaveTypeService {
    private final LeaveTypeRepository repository; private final LeaveTypeMapper mapper;
    public LeaveTypeService(LeaveTypeRepository repository, LeaveTypeMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<LeaveTypeResponse> list(UUID companyId) { return repository.findAll(companyId).stream().map(mapper::toResponse).toList(); }
    public LeaveTypeResponse getById(UUID leaveTypeId) { return repository.findById(leaveTypeId).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("LeaveType", leaveTypeId.toString())); }
    public LeaveTypeResponse create(LeaveTypeUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public LeaveTypeResponse update(UUID leaveTypeId, LeaveTypeUpsertRequest request) { return repository.update(leaveTypeId, request).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("LeaveType", leaveTypeId.toString())); }
    public void delete(UUID leaveTypeId) { if (!repository.delete(leaveTypeId)) throw new HrResourceNotFoundException("LeaveType", leaveTypeId.toString()); }
}
