package genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.mapper;

import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.dto.OvertimeRequestResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.overtime_request.model.OvertimeRequest;
import org.springframework.stereotype.Component;

@Component
public class OvertimeRequestMapper {
    public OvertimeRequestResponse toResponse(OvertimeRequest m) {
        return new OvertimeRequestResponse(m.overtimeId(), m.companyId(), m.companyCode(), m.employeeId(), m.projectId(), m.otDate(), m.hours(), m.otTypeId(), m.approved(), m.approvedBy(), m.approvedAt(), m.createdAt());
    }
}
