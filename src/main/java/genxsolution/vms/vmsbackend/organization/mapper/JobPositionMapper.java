package genxsolution.vms.vmsbackend.organization.mapper;

import genxsolution.vms.vmsbackend.organization.dto.jobposition.JobPositionResponse;
import genxsolution.vms.vmsbackend.organization.model.JobPosition;
import org.springframework.stereotype.Component;

@Component
public class JobPositionMapper {

    public JobPositionResponse toResponse(JobPosition model) {
        return new JobPositionResponse(
                model.positionId(),
                model.companyId(),
                model.companyCode(),
                model.positionCode(),
                model.positionName(),
                model.description(),
                model.isActive(),
                model.createdAt()
        );
    }
}






