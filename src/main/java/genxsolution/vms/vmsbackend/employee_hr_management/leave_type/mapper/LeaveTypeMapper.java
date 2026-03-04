package genxsolution.vms.vmsbackend.employee_hr_management.leave_type.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.dto.LeaveTypeResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.leave_type.model.LeaveType;
import org.springframework.stereotype.Component;

@Component
public class LeaveTypeMapper {
    public LeaveTypeResponse toResponse(LeaveType m) { return new LeaveTypeResponse(m.leaveTypeId(), m.companyId(), m.companyCode(), m.leaveCode(), m.leaveName(), m.daysPerYear()); }
}
