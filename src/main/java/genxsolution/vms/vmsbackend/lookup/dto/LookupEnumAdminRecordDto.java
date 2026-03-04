package genxsolution.vms.vmsbackend.lookup.dto;

public record LookupEnumAdminRecordDto(
        Integer id,
        String code,
        String name,
        String description,
        Boolean active
) {
}

