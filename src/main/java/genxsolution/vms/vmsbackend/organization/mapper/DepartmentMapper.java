package genxsolution.vms.vmsbackend.organization.mapper;

import genxsolution.vms.vmsbackend.organization.dto.department.DepartmentResponse;
import genxsolution.vms.vmsbackend.organization.model.Department;
import org.springframework.stereotype.Component;

@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department model) {
        return new DepartmentResponse(
                model.departmentId(),
                model.companyId(),
                model.companyCode(),
                model.branchId(),
                model.departmentCode(),
                model.departmentName(),
                model.parentDepartmentId(),
                model.isActive(),
                model.createdAt(),
                model.updatedAt()
        );
    }
}






