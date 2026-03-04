package genxsolution.vms.vmsbackend.lookup.dto;

import jakarta.validation.constraints.NotBlank;

public record LookupEnumAdminUpsertRequest(
        @NotBlank String code,
        @NotBlank String name,
        String description,
        Boolean active
) {
}

