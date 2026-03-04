package genxsolution.vms.vmsbackend.lookup.dto;

public record LookupEnumDefinitionDto(
        String key,
        String tableName,
        String displayName,
        boolean hasDescription,
        boolean hasActive
) {
}






