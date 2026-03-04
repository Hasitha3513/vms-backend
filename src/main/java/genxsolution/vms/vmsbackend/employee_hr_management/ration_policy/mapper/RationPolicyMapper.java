package genxsolution.vms.vmsbackend.employee_hr_management.ration_policy.mapper;
import genxsolution.vms.vmsbackend.employee_hr_management.ration_policy.dto.RationPolicyResponse; import genxsolution.vms.vmsbackend.employee_hr_management.ration_policy.model.RationPolicy; import org.springframework.stereotype.Component;
@Component public class RationPolicyMapper { public RationPolicyResponse toResponse(RationPolicy m){ return new RationPolicyResponse(m.rationPolicyId(),m.companyId(),m.companyCode(),m.policyName(),m.perDayAmount(),m.notes(),m.isActive(),m.createdAt()); } }
