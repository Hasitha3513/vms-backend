package genxsolution.vms.vmsbackend.organization.mapper;

import genxsolution.vms.vmsbackend.organization.dto.companybranch.CompanyBranchResponse;
import genxsolution.vms.vmsbackend.organization.model.CompanyBranch;
import org.springframework.stereotype.Component;

@Component
public class CompanyBranchMapper {

    public CompanyBranchResponse toResponse(CompanyBranch model) {
        return new CompanyBranchResponse(
                model.branchId(),
                model.companyId(),
                model.companyCode(),
                model.branchCode(),
                model.branchName(),
                model.address(),
                model.city(),
                model.stateProvince(),
                model.country(),
                model.latitude(),
                model.longitude(),
                model.isMainWorkshop(),
                model.isActive(),
                model.createdAt(),
                model.updatedAt()
        );
    }
}






