package genxsolution.vms.vmsbackend.employee_hr_management.leave_type.model;

import java.math.BigDecimal;
import java.util.UUID;

public record LeaveType(UUID leaveTypeId, UUID companyId, String companyCode, String leaveCode, String leaveName, BigDecimal daysPerYear) {}
