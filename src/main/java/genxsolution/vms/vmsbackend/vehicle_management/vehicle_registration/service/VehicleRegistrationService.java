package genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.mapper.VehicleRegistrationMapper;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.model.VehicleRegistration;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.repository.VehicleRegistrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class VehicleRegistrationService {
    private static final int REGISTRATION_TEXT_MAX_LENGTH = 255;
    private final VehicleRegistrationRepository repository;
    private final VehicleRegistrationMapper mapper;

    public VehicleRegistrationService(VehicleRegistrationRepository repository, VehicleRegistrationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<VehicleRegistrationResponse> list(UUID companyId, UUID companyVehicleId) {
        return repository.findAll(companyId, companyVehicleId).stream().map(mapper::toResponse).toList();
    }

    public VehicleRegistrationResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleRegistration", id.toString()));
    }

    public VehicleRegistrationPrefillResponse getPrefillByCompanyVehicle(UUID companyVehicleId) {
        UUID vehicleId = repository.findVehicleIdByCompanyVehicleId(companyVehicleId).orElse(null);
        UUID companyId = repository.findCompanyIdByCompanyVehicleId(companyVehicleId).orElse(null);
        VehicleRegistration latest = repository.findLatestByCompanyVehicleId(companyVehicleId).orElse(null);
        String existingRegistrationNumber = repository.findVehicleRegistrationNumberByCompanyVehicleId(companyVehicleId).orElse(null);

        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyVehicleId");
        }

        return new VehicleRegistrationPrefillResponse(
                vehicleId,
                companyId,
                companyVehicleId,
                latest == null ? existingRegistrationNumber : latest.registrationNumber(),
                latest == null ? null : latest.registrationDate(),
                latest == null ? null : latest.registrationExpiry(),
                latest == null ? null : latest.registeringAuthority(),
                latest == null ? null : latest.registrationState(),
                latest == null ? null : latest.registrationCity(),
                latest == null ? null : latest.rcBookNumber(),
                latest == null ? null : latest.rcStatus(),
                latest == null ? null : latest.numberPlateTypeId(),
                latest == null ? null : latest.renewalReminderDays(),
                latest == null ? null : latest.notes(),
                latest == null ? null : latest.isCurrent()
        );
    }

    @Transactional
    public VehicleRegistrationResponse create(VehicleRegistrationUpsertRequest r) {
        VehicleRegistrationUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        VehicleRegistration created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForCompanyVehicle(created.companyVehicleId(), created.registrationId());
        }
        syncCompanyVehicleRegistrationNumber(created.companyVehicleId(), created.registrationNumber());
        return mapper.toResponse(created);
    }

    @Transactional
    public VehicleRegistrationResponse update(UUID id, VehicleRegistrationUpsertRequest r) {
        VehicleRegistration existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleRegistration", id.toString()));
        if (isExpired(existing.registrationExpiry())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired vehicle registration is view-only");
        }
        VehicleRegistrationUpsertRequest merged = merge(existing, r);
        VehicleRegistrationUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForCompanyVehicle(updated.companyVehicleId(), updated.registrationId());
                    }
                    syncCompanyVehicleRegistrationNumber(updated.companyVehicleId(), updated.registrationNumber());
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleRegistration", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("VehicleRegistration", id.toString());
    }

    private VehicleRegistrationUpsertRequest prepareForCreate(VehicleRegistrationUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.companyVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyVehicleId is required");

        UUID companyId = r.companyId() != null ? r.companyId() : repository.findCompanyIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);

        return new VehicleRegistrationUpsertRequest(
                vehicleId,
                companyId,
                r.companyVehicleId(),
                r.registrationNumber(),
                r.registrationDate(),
                r.registrationExpiry(),
                r.registeringAuthority(),
                r.registrationState(),
                r.registrationCity(),
                r.rcBookNumber(),
                r.rcStatus() != null ? r.rcStatus() : "Valid",
                r.numberPlateTypeId(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 30,
                r.notes(),
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private VehicleRegistrationUpsertRequest prepareForUpdate(VehicleRegistrationUpsertRequest r) {
        return new VehicleRegistrationUpsertRequest(
                r.vehicleId(),
                r.companyId(),
                r.companyVehicleId(),
                r.registrationNumber(),
                r.registrationDate(),
                r.registrationExpiry(),
                r.registeringAuthority(),
                r.registrationState(),
                r.registrationCity(),
                r.rcBookNumber(),
                r.rcStatus() != null ? r.rcStatus() : "Valid",
                r.numberPlateTypeId(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 30,
                r.notes(),
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private void validate(VehicleRegistrationUpsertRequest r) {
        if (r.companyVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyVehicleId is required");
        if (r.companyId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required");
        if (!repository.companyVehicleBelongsToCompany(r.companyVehicleId(), r.companyId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected company vehicle does not belong to selected company");
        }
        if (r.registrationNumber() == null || r.registrationNumber().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "registrationNumber is required");
        }
        if (r.rcStatus() != null) {
            String s = r.rcStatus().trim();
            if (!s.equals("Valid") && !s.equals("Expired") && !s.equals("Suspended")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "rcStatus must be Valid, Expired or Suspended");
            }
        }
        validateMaxLength("registrationNumber", r.registrationNumber());
        validateMaxLength("registeringAuthority", r.registeringAuthority());
        validateMaxLength("registrationState", r.registrationState());
        validateMaxLength("registrationCity", r.registrationCity());
        validateMaxLength("rcBookNumber", r.rcBookNumber());
        validateMaxLength("rcStatus", r.rcStatus());
        validateMaxLength("notes", r.notes());
    }

    private VehicleRegistrationUpsertRequest merge(VehicleRegistration c, VehicleRegistrationUpsertRequest r) {
        return new VehicleRegistrationUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.companyId() != null ? r.companyId() : c.companyId(),
                r.companyVehicleId() != null ? r.companyVehicleId() : c.companyVehicleId(),
                r.registrationNumber() != null ? r.registrationNumber() : c.registrationNumber(),
                r.registrationDate() != null ? r.registrationDate() : c.registrationDate(),
                r.registrationExpiry() != null ? r.registrationExpiry() : c.registrationExpiry(),
                r.registeringAuthority() != null ? r.registeringAuthority() : c.registeringAuthority(),
                r.registrationState() != null ? r.registrationState() : c.registrationState(),
                r.registrationCity() != null ? r.registrationCity() : c.registrationCity(),
                r.rcBookNumber() != null ? r.rcBookNumber() : c.rcBookNumber(),
                r.rcStatus() != null ? r.rcStatus() : c.rcStatus(),
                r.numberPlateTypeId() != null ? r.numberPlateTypeId() : c.numberPlateTypeId(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : c.renewalReminderDays(),
                r.notes() != null ? r.notes() : c.notes(),
                r.isCurrent() != null ? r.isCurrent() : c.isCurrent()
        );
    }

    private void syncCompanyVehicleRegistrationNumber(UUID companyVehicleId, String registrationNumber) {
        if (companyVehicleId == null || registrationNumber == null || registrationNumber.trim().isEmpty()) return;
        repository.updateCompanyVehicleRegistrationNumber(companyVehicleId, registrationNumber.trim());
    }

    private void validateMaxLength(String fieldName, String value) {
        if (value != null && value.length() > REGISTRATION_TEXT_MAX_LENGTH) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    fieldName + " must be at most " + REGISTRATION_TEXT_MAX_LENGTH + " characters"
            );
        }
    }

    private boolean isExpired(LocalDate expiryDate) {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}
