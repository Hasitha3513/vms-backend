package genxsolution.vms.vmsbackend.lookup.mapper;

import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumDefinitionDto;
import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumAdminRecordDto;
import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;
import genxsolution.vms.vmsbackend.lookup.model.LookupEnumRecord;
import genxsolution.vms.vmsbackend.lookup.repository.LookupEnumRepository;
import org.springframework.stereotype.Component;

@Component
public class LookupEnumMapper {

    public LookupOptionDto toDto(LookupEnumRecord record) {
        return new LookupOptionDto(
                record.id(),
                record.code(),
                record.name(),
                record.description()
        );
    }

    public LookupEnumDefinitionDto toDefinitionDto(String key, LookupEnumRepository.EnumTableMeta meta) {
        return new LookupEnumDefinitionDto(
                key,
                meta.tableName(),
                meta.displayName(),
                meta.descriptionColumn() != null,
                meta.activeColumn() != null
        );
    }

    public LookupEnumAdminRecordDto toAdminDto(LookupEnumRecord record) {
        return new LookupEnumAdminRecordDto(
                record.id(),
                record.code(),
                record.name(),
                record.description(),
                record.active()
        );
    }
}






