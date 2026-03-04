package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.service;

import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto.CompanyVehicleIdTypeResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto.CompanyVehicleIdTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.mapper.CompanyVehicleIdTypeMapper;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.repository.CompanyVehicleIdTypeRepository;
import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class CompanyVehicleIdTypeService {
    private final CompanyVehicleIdTypeRepository repository;
    private final CompanyVehicleIdTypeMapper mapper;

    public CompanyVehicleIdTypeService(CompanyVehicleIdTypeRepository repository, CompanyVehicleIdTypeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<CompanyVehicleIdTypeResponse> list(UUID companyId) {
        return repository.findAll(companyId).stream().map(mapper::toResponse).toList();
    }

    public CompanyVehicleIdTypeResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("CompanyVehicleIdType", id.toString()));
    }

    public CompanyVehicleIdTypeResponse create(CompanyVehicleIdTypeUpsertRequest r) {
        validateRequired(r);
        String companyCode = resolveCompanyCode(r.companyId(), r.companyCode());
        validateUnique(r.companyId(), r.idTypeCode(), null);
        return mapper.toResponse(repository.create(new CompanyVehicleIdTypeUpsertRequest(
                r.companyId(),
                companyCode,
                r.typeId(),
                r.idTypeCode(),
                r.isActive()
        )));
    }

    public CompanyVehicleIdTypeResponse update(UUID id, CompanyVehicleIdTypeUpsertRequest r) {
        var existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("CompanyVehicleIdType", id.toString()));
        UUID companyId = r.companyId() != null ? r.companyId() : existing.companyId();
        String code = r.idTypeCode() != null ? r.idTypeCode() : existing.idTypeCode();
        String companyCode = resolveCompanyCode(companyId, r.companyCode() != null ? r.companyCode() : existing.companyCode());
        if (companyId == null || code == null || code.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId and idTypeCode are required");
        }
        validateUnique(companyId, code, id);
        return repository.update(id, new CompanyVehicleIdTypeUpsertRequest(
                        companyId,
                        companyCode,
                        r.typeId(),
                        r.idTypeCode(),
                        r.isActive()
                )).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("CompanyVehicleIdType", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("CompanyVehicleIdType", id.toString());
    }

    private void validateRequired(CompanyVehicleIdTypeUpsertRequest r) {
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
