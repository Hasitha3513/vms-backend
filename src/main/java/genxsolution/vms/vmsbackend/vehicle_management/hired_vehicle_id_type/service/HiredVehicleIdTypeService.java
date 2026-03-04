package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.service;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto.HiredVehicleIdTypeResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto.HiredVehicleIdTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.mapper.HiredVehicleIdTypeMapper;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.repository.HiredVehicleIdTypeRepository;
import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class HiredVehicleIdTypeService {
    private final HiredVehicleIdTypeRepository repository;
    private final HiredVehicleIdTypeMapper mapper;

    public HiredVehicleIdTypeService(HiredVehicleIdTypeRepository repository, HiredVehicleIdTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HiredVehicleIdTypeResponse> list(UUID companyId) {
        return repository.findAll(companyId).stream().map(mapper::toResponse).toList();
    }

    public HiredVehicleIdTypeResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleIdType", id.toString()));
    }

    public HiredVehicleIdTypeResponse create(HiredVehicleIdTypeUpsertRequest r) {
        validateRequired(r);
        String companyCode = resolveCompanyCode(r.companyId(), r.companyCode());
        validateUnique(r.companyId(), r.idTypeCode(), null);
        return mapper.toResponse(repository.create(new HiredVehicleIdTypeUpsertRequest(
                r.companyId(),
                companyCode,
                r.typeId(),
                r.idTypeCode(),
                r.isActive()
        )));
    }

    public HiredVehicleIdTypeResponse update(UUID id, HiredVehicleIdTypeUpsertRequest r) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleIdType", id.toString()));
        UUID companyId = r.companyId() != null ? r.companyId() : existing.companyId();
        String code = r.idTypeCode() != null ? r.idTypeCode() : existing.idTypeCode();
        String companyCode = resolveCompanyCode(companyId, r.companyCode() != null ? r.companyCode() : existing.companyCode());
        if (companyId == null || code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId and idTypeCode are required");
        }
        validateUnique(companyId, code, id);
        return repository.update(id, new HiredVehicleIdTypeUpsertRequest(
                        companyId,
                        companyCode,
                        r.typeId(),
                        r.idTypeCode(),
                        r.isActive()
                )).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleIdType", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("HiredVehicleIdType", id.toString());
    }

    private void validateRequired(HiredVehicleIdTypeUpsertRequest r) {
        if (r.companyId() == null || r.typeId() == null || r.idTypeCode() == null || r.idTypeCode().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId, typeId and idTypeCode are required");
        }
    }

    private void validateUnique(UUID companyId, String code, UUID excludeId) {
        if (repository.existsByCompanyAndCode(companyId, code, excludeId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Identity Type Code already exists for selected company");
        }
    }

    private String resolveCompanyCode(UUID companyId, String requestedCode) {
        String dbCode = repository.findCompanyCodeById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyId"));
        if (requestedCode == null || requestedCode.trim().isEmpty()) {
            return dbCode;
        }
        if (!dbCode.equalsIgnoreCase(requestedCode.trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected company and companyCode do not match");
        }
        return dbCode;
    }
}
