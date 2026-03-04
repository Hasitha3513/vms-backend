package genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificatePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificateResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificateUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.mapper.VehicleFitnessCertificateMapper;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.model.VehicleFitnessCertificate;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.repository.VehicleFitnessCertificateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleFitnessCertificateService {
    private final VehicleFitnessCertificateRepository repository;
    private final VehicleFitnessCertificateMapper mapper;

    public VehicleFitnessCertificateService(VehicleFitnessCertificateRepository repository, VehicleFitnessCertificateMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<VehicleFitnessCertificateResponse> list(UUID companyId, UUID companyVehicleId) {
        return repository.findAll(companyId, companyVehicleId).stream().map(mapper::toResponse).toList();
    }

    public VehicleFitnessCertificateResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleFitnessCertificate", id.toString()));
    }

    public VehicleFitnessCertificatePrefillResponse getPrefillByCompanyVehicle(UUID companyVehicleId) {
        UUID vehicleId = repository.findVehicleIdByCompanyVehicleId(companyVehicleId).orElse(null);
        UUID companyId = repository.findCompanyIdByCompanyVehicleId(companyVehicleId).orElse(null);
        VehicleFitnessCertificate latest = repository.findLatestByCompanyVehicleId(companyVehicleId).orElse(null);

        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyVehicleId");
        }

        return new VehicleFitnessCertificatePrefillResponse(
                vehicleId,
                companyId,
                companyVehicleId,
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

    public VehicleFitnessCertificateResponse create(VehicleFitnessCertificateUpsertRequest r) {
        VehicleFitnessCertificateUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        VehicleFitnessCertificate created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForCompanyVehicle(created.companyVehicleId(), created.fitnessId());
        }
        return mapper.toResponse(created);
    }

    public VehicleFitnessCertificateResponse update(UUID id, VehicleFitnessCertificateUpsertRequest r) {
        VehicleFitnessCertificate existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleFitnessCertificate", id.toString()));
        VehicleFitnessCertificateUpsertRequest merged = merge(existing, r);
        VehicleFitnessCertificateUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForCompanyVehicle(updated.companyVehicleId(), updated.fitnessId());
                    }
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleFitnessCertificate", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("VehicleFitnessCertificate", id.toString());
    }

    private VehicleFitnessCertificateUpsertRequest prepareForCreate(VehicleFitnessCertificateUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.companyVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyVehicleId is required");

        UUID companyId = r.companyId() != null ? r.companyId() : repository.findCompanyIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);

        return new VehicleFitnessCertificateUpsertRequest(
                vehicleId,
                companyId,
                r.companyVehicleId(),
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

    private VehicleFitnessCertificateUpsertRequest prepareForUpdate(VehicleFitnessCertificateUpsertRequest r) {
        return new VehicleFitnessCertificateUpsertRequest(
                r.vehicleId(),
                r.companyId(),
                r.companyVehicleId(),
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

    private void validate(VehicleFitnessCertificateUpsertRequest r) {
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
        if (r.fitnessStatus() != null) {
            String s = r.fitnessStatus().trim();
            if (!s.equals("Valid") && !s.equals("Expired") && !s.equals("Suspended")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fitnessStatus must be Valid, Expired or Suspended");
            }
        }
    }

    private VehicleFitnessCertificateUpsertRequest merge(VehicleFitnessCertificate c, VehicleFitnessCertificateUpsertRequest r) {
        return new VehicleFitnessCertificateUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.companyId() != null ? r.companyId() : c.companyId(),
                r.companyVehicleId() != null ? r.companyVehicleId() : c.companyVehicleId(),
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
