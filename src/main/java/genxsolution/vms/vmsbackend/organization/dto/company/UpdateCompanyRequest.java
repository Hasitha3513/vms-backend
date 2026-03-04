package genxsolution.vms.vmsbackend.organization.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UpdateCompanyRequest(
        @Size(max = 200) String companyName,
        Integer companyTypeId,
        @Size(max = 100) String registrationNo,
        @Size(max = 100) String taxId,
        @Email @Size(max = 120) String email,
        @Size(max = 20) String phonePrimary,
        String address,
        @Size(max = 50) String timezone,
        @Size(max = 10) String currency,
        Boolean isActive
) {
}






