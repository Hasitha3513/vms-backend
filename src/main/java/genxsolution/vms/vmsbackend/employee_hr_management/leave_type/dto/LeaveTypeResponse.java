package genxsolution.vms.vmsbackend.employee_hr_management.leave_type.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record LeaveTypeResponse(UUID leaveTypeId, UUID companyId, String companyCode, String leaveCode, String leaveName, BigDecimal daysPerYear) {}
