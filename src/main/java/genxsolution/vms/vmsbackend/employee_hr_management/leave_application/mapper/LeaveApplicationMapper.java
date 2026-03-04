package genxsolution.vms.vmsbackend.employee_hr_management.leave_application.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.dto.LeaveApplicationResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_application.model.LeaveApplication;
import org.springframework.stereotype.Component;

@Component
public class LeaveApplicationMapper {
    public LeaveApplicationResponse toResponse(LeaveApplication m) {
        return new LeaveApplicationResponse(m.leaveId(), m.companyId(), m.companyCode(), m.employeeId(), m.leaveTypeId(), m.startDate(), m.endDate(), m.totalDays(), m.statusId(), m.appliedAt(), m.approvedBy(), m.approvedAt(), m.updatedAt());
    }
}
