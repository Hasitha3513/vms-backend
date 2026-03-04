package genxsolution.vms.vmsbackend.organization.mapper;

import genxsolution.vms.vmsbackend.organization.dto.company.CompanyResponse;
import genxsolution.vms.vmsbackend.organization.model.Company;
import org.springframework.stereotype.Component;

@Component
public class CompanyMapper {

    public CompanyResponse toResponse(Company model) {
        return new CompanyResponse(
                model.companyId(),
                model.companyCode(),
                model.companyName(),
                model.companyTypeId(),
                model.registrationNo(),
                model.taxId(),
                model.email(),
                model.phonePrimary(),
                model.address(),
                model.timezone(),
                model.currency(),
                model.isActive(),
                model.createdAt(),
                model.updatedAt()
        );
    }
}






