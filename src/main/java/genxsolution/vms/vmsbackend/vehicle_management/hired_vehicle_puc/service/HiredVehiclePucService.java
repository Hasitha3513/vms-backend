package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.mapper.HiredVehiclePucMapper;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.model.HiredVehiclePuc;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.repository.HiredVehiclePucRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class HiredVehiclePucService {
    private final HiredVehiclePucRepository repository;
    private final HiredVehiclePucMapper mapper;

    public HiredVehiclePucService(HiredVehiclePucRepository repository, HiredVehiclePucMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HiredVehiclePucResponse> list(UUID supplierId, UUID hiredVehicleId) {
        return repository.findAll(supplierId, hiredVehicleId).stream().map(mapper::toResponse).toList();
    }

    public HiredVehiclePucResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehiclePuc", id.toString()));
    }

    public HiredVehiclePucPrefillResponse getPrefillByHiredVehicle(UUID hiredVehicleId) {
        UUID vehicleId = repository.findVehicleIdByHiredVehicleId(hiredVehicleId).orElse(null);
        UUID supplierId = repository.findSupplierIdByHiredVehicleId(hiredVehicleId).orElse(null);
        HiredVehiclePuc latest = repository.findLatestByHiredVehicleId(hiredVehicleId).orElse(null);

        if (supplierId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hiredVehicleId");
        }

        return new HiredVehiclePucPrefillResponse(
                vehicleId,
                supplierId,
                hiredVehicleId,
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

    public HiredVehiclePucResponse create(HiredVehiclePucUpsertRequest r) {
        HiredVehiclePucUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        HiredVehiclePuc created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForHiredVehicle(created.hiredVehicleId(), created.pucId());
        }
        return mapper.toResponse(created);
    }

    public HiredVehiclePucResponse update(UUID id, HiredVehiclePucUpsertRequest r) {
        HiredVehiclePuc existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehiclePuc", id.toString()));
        HiredVehiclePucUpsertRequest merged = merge(existing, r);
        HiredVehiclePucUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForHiredVehicle(updated.hiredVehicleId(), updated.pucId());
                    }
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehiclePuc", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("HiredVehiclePuc", id.toString());
    }

    private HiredVehiclePucUpsertRequest prepareForCreate(HiredVehiclePucUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.hiredVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hiredVehicleId is required");

        UUID supplierId = r.supplierId() != null ? r.supplierId() : repository.findSupplierIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);

        return new HiredVehiclePucUpsertRequest(
                vehicleId,
                supplierId,
                r.hiredVehicleId(),
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

    private HiredVehiclePucUpsertRequest prepareForUpdate(HiredVehiclePucUpsertRequest r) {
        return new HiredVehiclePucUpsertRequest(
                r.vehicleId(),
                r.supplierId(),
                r.hiredVehicleId(),
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

    private void validate(HiredVehiclePucUpsertRequest r) {
        if (r.hiredVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hiredVehicleId is required");
        if (r.supplierId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "supplierId is required");
        if (!repository.HiredVehicleBelongsToCompany(r.hiredVehicleId(), r.supplierId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected hired vehicle does not belong to selected supplier");
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

    private HiredVehiclePucUpsertRequest merge(HiredVehiclePuc c, HiredVehiclePucUpsertRequest r) {
        return new HiredVehiclePucUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.supplierId() != null ? r.supplierId() : c.supplierId(),
                r.hiredVehicleId() != null ? r.hiredVehicleId() : c.hiredVehicleId(),
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



