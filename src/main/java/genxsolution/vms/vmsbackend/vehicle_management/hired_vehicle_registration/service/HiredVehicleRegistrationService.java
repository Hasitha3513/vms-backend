package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.mapper.HiredVehicleRegistrationMapper;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.model.HiredVehicleRegistration;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.repository.HiredVehicleRegistrationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class HiredVehicleRegistrationService {
    private static final int REGISTRATION_TEXT_MAX_LENGTH = 255;
    private final HiredVehicleRegistrationRepository repository;
    private final HiredVehicleRegistrationMapper mapper;

    public HiredVehicleRegistrationService(HiredVehicleRegistrationRepository repository, HiredVehicleRegistrationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HiredVehicleRegistrationResponse> list(UUID supplierId, UUID hiredVehicleId) {
        return repository.findAll(supplierId, hiredVehicleId).stream().map(mapper::toResponse).toList();
    }

    public HiredVehicleRegistrationResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleRegistration", id.toString()));
    }

    public HiredVehicleRegistrationPrefillResponse getPrefillByHiredVehicle(UUID hiredVehicleId) {
        UUID vehicleId = repository.findVehicleIdByHiredVehicleId(hiredVehicleId).orElse(null);
        UUID supplierId = repository.findSupplierIdByHiredVehicleId(hiredVehicleId).orElse(null);
        HiredVehicleRegistration latest = repository.findLatestByHiredVehicleId(hiredVehicleId).orElse(null);
        String existingRegistrationNumber = repository.findHiredVehicleRegistrationNumberByHiredVehicleId(hiredVehicleId).orElse(null);

        if (supplierId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hiredVehicleId");
        }

        return new HiredVehicleRegistrationPrefillResponse(
                vehicleId,
                supplierId,
                hiredVehicleId,
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
    public HiredVehicleRegistrationResponse create(HiredVehicleRegistrationUpsertRequest r) {
        HiredVehicleRegistrationUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        HiredVehicleRegistration created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForHiredVehicle(created.hiredVehicleId(), created.registrationId());
        }
        syncCompanyHiredVehicleRegistrationNumber(created.hiredVehicleId(), created.registrationNumber());
        return mapper.toResponse(created);
    }

    @Transactional
    public HiredVehicleRegistrationResponse update(UUID id, HiredVehicleRegistrationUpsertRequest r) {
        HiredVehicleRegistration existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleRegistration", id.toString()));
        if (isExpired(existing.registrationExpiry())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired vehicle registration is view-only");
        }
        HiredVehicleRegistrationUpsertRequest merged = merge(existing, r);
        HiredVehicleRegistrationUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForHiredVehicle(updated.hiredVehicleId(), updated.registrationId());
                    }
                    syncCompanyHiredVehicleRegistrationNumber(updated.hiredVehicleId(), updated.registrationNumber());
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleRegistration", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("HiredVehicleRegistration", id.toString());
    }

    private HiredVehicleRegistrationUpsertRequest prepareForCreate(HiredVehicleRegistrationUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.hiredVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hiredVehicleId is required");

        UUID supplierId = r.supplierId() != null ? r.supplierId() : repository.findSupplierIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);

        return new HiredVehicleRegistrationUpsertRequest(
                vehicleId,
                supplierId,
                r.hiredVehicleId(),
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

    private HiredVehicleRegistrationUpsertRequest prepareForUpdate(HiredVehicleRegistrationUpsertRequest r) {
        return new HiredVehicleRegistrationUpsertRequest(
                r.vehicleId(),
                r.supplierId(),
                r.hiredVehicleId(),
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

    private void validate(HiredVehicleRegistrationUpsertRequest r) {
        if (r.hiredVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hiredVehicleId is required");
        if (r.supplierId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "supplierId is required");
        if (!repository.HiredVehicleBelongsToCompany(r.hiredVehicleId(), r.supplierId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected hired vehicle does not belong to selected supplier");
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

    private HiredVehicleRegistrationUpsertRequest merge(HiredVehicleRegistration c, HiredVehicleRegistrationUpsertRequest r) {
        return new HiredVehicleRegistrationUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.supplierId() != null ? r.supplierId() : c.supplierId(),
                r.hiredVehicleId() != null ? r.hiredVehicleId() : c.hiredVehicleId(),
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

    private void syncCompanyHiredVehicleRegistrationNumber(UUID hiredVehicleId, String registrationNumber) {
        if (hiredVehicleId == null || registrationNumber == null || registrationNumber.trim().isEmpty()) return;
        repository.updateCompanyHiredVehicleRegistrationNumber(hiredVehicleId, registrationNumber.trim());
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



