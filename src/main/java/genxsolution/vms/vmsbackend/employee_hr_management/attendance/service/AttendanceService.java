package genxsolution.vms.vmsbackend.employee_hr_management.attendance.service;

import genxsolution.vms.vmsbackend.employee_hr_management.attendance.dto.AttendanceResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.dto.AttendanceUpsertRequest;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.mapper.AttendanceMapper;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.repository.AttendanceRepository;
import genxsolution.vms.vmsbackend.employee_hr_management.exception.HrResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AttendanceService {
    private final AttendanceRepository repository; private final AttendanceMapper mapper;
    public AttendanceService(AttendanceRepository repository, AttendanceMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<AttendanceResponse> list(UUID companyId) { return repository.findAll(companyId).stream().map(mapper::toResponse).toList(); }
    public AttendanceResponse getById(UUID attendanceId) { return repository.findById(attendanceId).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("Attendance", attendanceId.toString())); }
    public AttendanceResponse create(AttendanceUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public AttendanceResponse update(UUID attendanceId, AttendanceUpsertRequest request) { return repository.update(attendanceId, request).map(mapper::toResponse).orElseThrow(() -> new HrResourceNotFoundException("Attendance", attendanceId.toString())); }
    public void delete(UUID attendanceId) { if (!repository.delete(attendanceId)) throw new HrResourceNotFoundException("Attendance", attendanceId.toString()); }
}
