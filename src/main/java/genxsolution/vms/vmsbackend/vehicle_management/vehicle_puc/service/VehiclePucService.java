package genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.mapper.VehiclePucMapper;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.model.VehiclePuc;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.repository.VehiclePucRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class VehiclePucService {
    private final VehiclePucRepository repository;
    private final VehiclePucMapper mapper;

    public VehiclePucService(VehiclePucRepository repository, VehiclePucMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<VehiclePucResponse> list(UUID companyId, UUID companyVehicleId) {
        return repository.findAll(companyId, companyVehicleId).stream().map(mapper::toResponse).toList();
    }

    public VehiclePucResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehiclePuc", id.toString()));
    }

    public VehiclePucPrefillResponse getPrefillByCompanyVehicle(UUID companyVehicleId) {
        UUID vehicleId = repository.findVehicleIdByCompanyVehicleId(companyVehicleId).orElse(null);
        UUID companyId = repository.findCompanyIdByCompanyVehicleId(companyVehicleId).orElse(null);
        VehiclePuc latest = repository.findLatestByCompanyVehicleId(companyVehicleId).orElse(null);

        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyVehicleId");
        }

        return new VehiclePucPrefillResponse(
                vehicleId,
                companyId,
                companyVehicleId,
                latest == null ? null : latest.certificateNumber(),
                latest == null ? null : latest.issuingCenter(),
                latest == null ? null : latest.issueDate(),
                latest == null ? null : latest.expiryDate(),
                latest == null ? null : latest.coEmissionPercent(),
                latest == null ? null : latest.hcEmissionPpm(),
                latest == null ? "Pass" : latest.testResult(),
                latest == null ? "Valid" : latest.pucStatus(),
                latest == null ? 15 : latest.renewalReminderDays(),
                latest == null ? Boolean.TRUE : latest.isCurrent()
        );
    }

    public VehiclePucResponse create(VehiclePucUpsertRequest r) {
        VehiclePucUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        VehiclePuc created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForCompanyVehicle(created.companyVehicleId(), created.pucId());
        }
        return mapper.toResponse(created);
    }

    public VehiclePucResponse update(UUID id, VehiclePucUpsertRequest r) {
        VehiclePuc existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehiclePuc", id.toString()));
        VehiclePucUpsertRequest merged = merge(existing, r);
        VehiclePucUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForCompanyVehicle(updated.companyVehicleId(), updated.pucId());
                    }
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehiclePuc", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("VehiclePuc", id.toString());
    }

    private VehiclePucUpsertRequest prepareForCreate(VehiclePucUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.companyVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyVehicleId is required");

        UUID companyId = r.companyId() != null ? r.companyId() : repository.findCompanyIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);

        return new VehiclePucUpsertRequest(
                vehicleId,
                companyId,
                r.companyVehicleId(),
                r.certificateNumber(),
                r.issuingCenter(),
                r.issueDate(),
                r.expiryDate(),
                r.coEmissionPercent(),
                r.hcEmissionPpm(),
                r.testResult() != null ? r.testResult() : "Pass",
                r.pucStatus() != null ? r.pucStatus() : "Valid",
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 15,
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private VehiclePucUpsertRequest prepareForUpdate(VehiclePucUpsertRequest r) {
        return new VehiclePucUpsertRequest(
                r.vehicleId(),
                r.companyId(),
                r.companyVehicleId(),
                r.certificateNumber(),
                r.issuingCenter(),
                r.issueDate(),
                r.expiryDate(),
                r.coEmissionPercent(),
                r.hcEmissionPpm(),
                r.testResult() != null ? r.testResult() : "Pass",
                r.pucStatus() != null ? r.pucStatus() : "Valid",
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 15,
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private void validate(VehiclePucUpsertRequest r) {
        if (r.companyVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyVehicleId is required");
        if (r.companyId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required");
        if (!repository.companyVehicleBelongsToCompany(r.companyVehicleId(), r.companyId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected company vehicle does not belong to selected company");
        }
        if (r.certificateNumber() == null || r.certificateNumber().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "certificateNumber is required");
        }
        if (r.issueDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "issueDate is required");
        }
        if (r.expiryDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "expiryDate is required");
        }
        if (r.testResult() != null) {
            String s = r.testResult().trim();
            if (!s.equals("Pass") && !s.equals("Fail")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "testResult must be Pass or Fail");
            }
        }
        if (r.pucStatus() != null) {
            String s = r.pucStatus().trim();
            if (!s.equals("Valid") && !s.equals("Expired")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "pucStatus must be Valid or Expired");
            }
        }
    }

    private VehiclePucUpsertRequest merge(VehiclePuc c, VehiclePucUpsertRequest r) {
        return new VehiclePucUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.companyId() != null ? r.companyId() : c.companyId(),
                r.companyVehicleId() != null ? r.companyVehicleId() : c.companyVehicleId(),
                r.certificateNumber() != null ? r.certificateNumber() : c.certificateNumber(),
                r.issuingCenter() != null ? r.issuingCenter() : c.issuingCenter(),
                r.issueDate() != null ? r.issueDate() : c.issueDate(),
                r.expiryDate() != null ? r.expiryDate() : c.expiryDate(),
                r.coEmissionPercent() != null ? r.coEmissionPercent() : c.coEmissionPercent(),
                r.hcEmissionPpm() != null ? r.hcEmissionPpm() : c.hcEmissionPpm(),
                r.testResult() != null ? r.testResult() : c.testResult(),
                r.pucStatus() != null ? r.pucStatus() : c.pucStatus(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : c.renewalReminderDays(),
                r.isCurrent() != null ? r.isCurrent() : c.isCurrent()
        );
    }
}
