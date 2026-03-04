package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificatePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificateResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificateUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.mapper.HiredVehicleFitnessCertificateMapper;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.model.HiredVehicleFitnessCertificate;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.repository.HiredVehicleFitnessCertificateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class HiredVehicleFitnessCertificateService {
    private final HiredVehicleFitnessCertificateRepository repository;
    private final HiredVehicleFitnessCertificateMapper mapper;

    public HiredVehicleFitnessCertificateService(HiredVehicleFitnessCertificateRepository repository, HiredVehicleFitnessCertificateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HiredVehicleFitnessCertificateResponse> list(UUID supplierId, UUID hiredVehicleId) {
        return repository.findAll(supplierId, hiredVehicleId).stream().map(mapper::toResponse).toList();
    }

    public HiredVehicleFitnessCertificateResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleFitnessCertificate", id.toString()));
    }

    public HiredVehicleFitnessCertificatePrefillResponse getPrefillByHiredVehicle(UUID hiredVehicleId) {
        UUID vehicleId = repository.findVehicleIdByHiredVehicleId(hiredVehicleId).orElse(null);
        UUID supplierId = repository.findSupplierIdByHiredVehicleId(hiredVehicleId).orElse(null);
        HiredVehicleFitnessCertificate latest = repository.findLatestByHiredVehicleId(hiredVehicleId).orElse(null);

        if (supplierId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hiredVehicleId");
        }

        return new HiredVehicleFitnessCertificatePrefillResponse(
                vehicleId,
                supplierId,
                hiredVehicleId,
                latest == null ? null : latest.certificateNumber(),
                latest == null ? null : latest.issuingAuthority(),
                latest == null ? null : latest.inspectionCenter(),
                latest == null ? null : latest.inspectorId(),
                latest == null ? null : latest.inspectorName(),
                latest == null ? null : latest.issueDate(),
                latest == null ? null : latest.expiryDate(),
                latest == null ? null : latest.validityDurationYears(),
                latest == null ? null : latest.inspectionResultId(),
                latest == null ? null : latest.remarks(),
                latest == null ? 30 : latest.renewalReminderDays(),
                latest == null ? "Valid" : latest.fitnessStatus(),
                latest == null ? Boolean.TRUE : latest.isCurrent()
        );
    }

    public HiredVehicleFitnessCertificateResponse create(HiredVehicleFitnessCertificateUpsertRequest r) {
        HiredVehicleFitnessCertificateUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        HiredVehicleFitnessCertificate created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForHiredVehicle(created.hiredVehicleId(), created.fitnessId());
        }
        return mapper.toResponse(created);
    }

    public HiredVehicleFitnessCertificateResponse update(UUID id, HiredVehicleFitnessCertificateUpsertRequest r) {
        HiredVehicleFitnessCertificate existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleFitnessCertificate", id.toString()));
        HiredVehicleFitnessCertificateUpsertRequest merged = merge(existing, r);
        HiredVehicleFitnessCertificateUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForHiredVehicle(updated.hiredVehicleId(), updated.fitnessId());
                    }
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleFitnessCertificate", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("HiredVehicleFitnessCertificate", id.toString());
    }

    private HiredVehicleFitnessCertificateUpsertRequest prepareForCreate(HiredVehicleFitnessCertificateUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.hiredVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hiredVehicleId is required");

        UUID supplierId = r.supplierId() != null ? r.supplierId() : repository.findSupplierIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);

        return new HiredVehicleFitnessCertificateUpsertRequest(
                vehicleId,
                supplierId,
                r.hiredVehicleId(),
                r.certificateNumber(),
                r.issuingAuthority(),
                r.inspectionCenter(),
                r.inspectorId(),
                r.inspectorName(),
                r.issueDate(),
                r.expiryDate(),
                r.validityDurationYears(),
                r.inspectionResultId(),
                r.remarks(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 30,
                r.fitnessStatus() != null ? r.fitnessStatus() : "Valid",
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private HiredVehicleFitnessCertificateUpsertRequest prepareForUpdate(HiredVehicleFitnessCertificateUpsertRequest r) {
        return new HiredVehicleFitnessCertificateUpsertRequest(
                r.vehicleId(),
                r.supplierId(),
                r.hiredVehicleId(),
                r.certificateNumber(),
                r.issuingAuthority(),
                r.inspectionCenter(),
                r.inspectorId(),
                r.inspectorName(),
                r.issueDate(),
                r.expiryDate(),
                r.validityDurationYears(),
                r.inspectionResultId(),
                r.remarks(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 30,
                r.fitnessStatus() != null ? r.fitnessStatus() : "Valid",
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private void validate(HiredVehicleFitnessCertificateUpsertRequest r) {
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
        if (r.fitnessStatus() != null) {
            String s = r.fitnessStatus().trim();
            if (!s.equals("Valid") && !s.equals("Expired") && !s.equals("Suspended")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fitnessStatus must be Valid, Expired or Suspended");
            }
        }
    }

    private HiredVehicleFitnessCertificateUpsertRequest merge(HiredVehicleFitnessCertificate c, HiredVehicleFitnessCertificateUpsertRequest r) {
        return new HiredVehicleFitnessCertificateUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.supplierId() != null ? r.supplierId() : c.supplierId(),
                r.hiredVehicleId() != null ? r.hiredVehicleId() : c.hiredVehicleId(),
                r.certificateNumber() != null ? r.certificateNumber() : c.certificateNumber(),
                r.issuingAuthority() != null ? r.issuingAuthority() : c.issuingAuthority(),
                r.inspectionCenter() != null ? r.inspectionCenter() : c.inspectionCenter(),
                r.inspectorId() != null ? r.inspectorId() : c.inspectorId(),
                r.inspectorName() != null ? r.inspectorName() : c.inspectorName(),
                r.issueDate() != null ? r.issueDate() : c.issueDate(),
                r.expiryDate() != null ? r.expiryDate() : c.expiryDate(),
                r.validityDurationYears() != null ? r.validityDurationYears() : c.validityDurationYears(),
                r.inspectionResultId() != null ? r.inspectionResultId() : c.inspectionResultId(),
                r.remarks() != null ? r.remarks() : c.remarks(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : c.renewalReminderDays(),
                r.fitnessStatus() != null ? r.fitnessStatus() : c.fitnessStatus(),
                r.isCurrent() != null ? r.isCurrent() : c.isCurrent()
        );
    }
}



