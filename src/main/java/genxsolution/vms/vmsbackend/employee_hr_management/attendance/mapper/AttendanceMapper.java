package genxsolution.vms.vmsbackend.employee_hr_management.attendance.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.attendance.dto.AttendanceResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.attendance.model.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {
    public AttendanceResponse toResponse(Attendance m) {
        return new AttendanceResponse(m.attendanceId(), m.companyId(), m.companyCode(), m.employeeId(), m.attendanceDate(),
                m.checkInTime(), m.checkOutTime(), m.projectId(), m.latitudeIn(), m.longitudeIn(), m.latitudeOut(), m.longitudeOut(),
                m.scheduledHours(), m.actualHours(), m.overtimeHours(), m.statusId());
    }
}
