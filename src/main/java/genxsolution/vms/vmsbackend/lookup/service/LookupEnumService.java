package genxsolution.vms.vmsbackend.lookup.service;

import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumDefinitionDto;
import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumAdminRecordDto;
import genxsolution.vms.vmsbackend.lookup.dto.LookupEnumAdminUpsertRequest;
import genxsolution.vms.vmsbackend.lookup.dto.LookupOptionDto;
import genxsolution.vms.vmsbackend.lookup.mapper.LookupEnumMapper;
import genxsolution.vms.vmsbackend.lookup.repository.LookupEnumRepository;
import genxsolution.vms.vmsbackend.common.cache.CacheNames;
import genxsolution.vms.vmsbackend.lookup.exception.LookupResourceNotFoundException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LookupEnumService {

    private final LookupEnumRepository repository;
    private final LookupEnumMapper mapper;

    public LookupEnumService(LookupEnumRepository repository, LookupEnumMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Cacheable(cacheNames = CacheNames.LOOKUP_ENUM_VALUES, key = "#enumKey + ':' + #activeOnly")
    public List<LookupOptionDto> getEnumValues(String enumKey, boolean activeOnly) {
        return repository.findByEnumKey(enumKey, activeOnly).stream()
                .map(mapper::toDto)
                .toList();
    }

    public List<LookupEnumDefinitionDto> getSupportedEnums() {
        return repository.listSupportedEnums().entrySet().stream()
                .map(this::toDefinition)
                .toList();
    }

    public List<LookupEnumAdminRecordDto> getAdminRecords(String enumKey) {
        return repository.findAdminRecords(enumKey).stream()
                .map(mapper::toAdminDto)
                .toList();
    }

    public LookupEnumAdminRecordDto getAdminRecordById(String enumKey, Integer id) {
        var record = repository.findAdminRecordById(enumKey, id);
        if (record == null) {
            throw new LookupResourceNotFoundException("Enum record", enumKey + ":" + id);
        }
        return mapper.toAdminDto(record);
    }

    @CacheEvict(cacheNames = CacheNames.LOOKUP_ENUM_VALUES, allEntries = true)
    public LookupEnumAdminRecordDto createAdminRecord(String enumKey, LookupEnumAdminUpsertRequest request) {
        return mapper.toAdminDto(repository.createAdminRecord(
                enumKey,
                request.code(),
                request.name(),
                request.description(),
                request.active()
        ));
    }

    @CacheEvict(cacheNames = CacheNames.LOOKUP_ENUM_VALUES, allEntries = true)
    public LookupEnumAdminRecordDto updateAdminRecord(String enumKey, Integer id, LookupEnumAdminUpsertRequest request) {
        var record = repository.updateAdminRecord(
                enumKey,
                id,
                request.code(),
                request.name(),
                request.description(),
                request.active()
        );
        if (record == null) {
            throw new LookupResourceNotFoundException("Enum record", enumKey + ":" + id);
        }
        return mapper.toAdminDto(record);
    }

    @CacheEvict(cacheNames = CacheNames.LOOKUP_ENUM_VALUES, allEntries = true)
    public void deleteAdminRecord(String enumKey, Integer id) {
        if (!repository.deleteAdminRecord(enumKey, id)) {
            throw new LookupResourceNotFoundException("Enum record", enumKey + ":" + id);
        }
    }

    private LookupEnumDefinitionDto toDefinition(Map.Entry<String, LookupEnumRepository.EnumTableMeta> entry) {
        return mapper.toDefinitionDto(entry.getKey(), entry.getValue());
    }
}






