package genxsolution.vms.vmsbackend.organization.dto.company;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCompanyRequest(
        @NotBlank @Size(max = 50) String companyCode,
        @NotBlank @Size(max = 200) String companyName,
        Integer companyTypeId,
        @NotBlank @Size(max = 100) String registrationNo,
        @Size(max = 100) String taxId,
        @NotBlank @Email @Size(max = 120) String email,
        @NotBlank @Size(max = 20) String phonePrimary,
        String address,
        @Size(max = 50) String timezone,
        @Size(max = 10) String currency,
        Boolean isActive
) {
}






