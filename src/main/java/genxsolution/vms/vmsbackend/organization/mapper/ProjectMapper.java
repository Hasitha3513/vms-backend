package genxsolution.vms.vmsbackend.organization.mapper;

import genxsolution.vms.vmsbackend.organization.dto.project.ProjectResponse;
import genxsolution.vms.vmsbackend.organization.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project model) {
        return new ProjectResponse(
                model.projectId(),
                model.companyId(),
                model.companyCode(),
                model.branchId(),
                model.projectCode(),
                model.projectName(),
                model.projectTypeId(),
                model.siteAddress(),
                model.siteLatitude(),
                model.siteLongitude(),
                model.startDate(),
                model.plannedEndDate(),
                model.actualEndDate(),
                model.budgetAmount(),
                model.actualCost(),
                model.projectManager(),
                model.statusId(),
                model.createdAt(),
                model.updatedAt()
        );
    }
}






