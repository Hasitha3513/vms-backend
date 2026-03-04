package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsurancePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsuranceResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsuranceSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsuranceUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.mapper.HiredVehicleInsuranceMapper;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.model.HiredVehicleInsurance;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.repository.HiredVehicleInsuranceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class HiredVehicleInsuranceService {
    private final HiredVehicleInsuranceRepository repository;
    private final HiredVehicleInsuranceMapper mapper;

    public HiredVehicleInsuranceService(HiredVehicleInsuranceRepository repository, HiredVehicleInsuranceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HiredVehicleInsuranceResponse> list(UUID supplierId, UUID hiredVehicleId) {
        return repository.findAll(supplierId, hiredVehicleId).stream().map(mapper::toResponse).toList();
    }

    public HiredVehicleInsuranceResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleInsurance", id.toString()));
    }

    public List<HiredVehicleInsuranceSupplierOptionResponse> supplierOptions(UUID supplierId) {
        return repository.findInsuranceSupplierOptions(supplierId);
    }

    public HiredVehicleInsurancePrefillResponse getPrefillByHiredVehicle(UUID hiredVehicleId) {
        UUID vehicleId = repository.findVehicleIdByHiredVehicleId(hiredVehicleId).orElse(null);
        UUID supplierId = repository.findSupplierIdByHiredVehicleId(hiredVehicleId).orElse(null);
        HiredVehicleInsurance latest = repository.findLatestByHiredVehicleId(hiredVehicleId).orElse(null);

        if (supplierId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid hiredVehicleId");
        }

        return new HiredVehicleInsurancePrefillResponse(
                vehicleId,
                supplierId,
                hiredVehicleId,
                latest == null ? null : latest.insuranceCompany(),
                latest == null ? null : latest.policyNumber(),
                latest == null ? null : latest.insuranceTypeId(),
                latest == null ? null : latest.policyStartDate(),
                latest == null ? null : latest.policyExpiryDate(),
                latest == null ? null : latest.idvAmount(),
                latest == null ? null : latest.premiumAmount(),
                latest == null ? null : latest.paymentMode(),
                latest == null ? null : latest.paymentDate(),
                latest == null ? null : latest.agentName(),
                latest == null ? null : latest.agentContact(),
                latest == null ? null : latest.agentEmail(),
                latest == null ? null : latest.nomineeName(),
                latest == null ? null : latest.addOnCovers(),
                latest == null ? null : latest.ncbPercent(),
                latest == null ? null : latest.claimCount(),
                latest == null ? null : latest.lastClaimDate(),
                latest == null ? null : latest.lastClaimAmount(),
                latest == null ? 30 : latest.renewalReminderDays(),
                latest == null ? "Active" : latest.insuranceStatus(),
                latest == null ? null : latest.notes(),
                latest == null ? Boolean.TRUE : latest.isCurrent()
        );
    }

    public HiredVehicleInsuranceResponse create(HiredVehicleInsuranceUpsertRequest r) {
        HiredVehicleInsuranceUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        HiredVehicleInsurance created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForHiredVehicle(created.hiredVehicleId(), created.insuranceId());
        }
        return mapper.toResponse(created);
    }

    public HiredVehicleInsuranceResponse update(UUID id, HiredVehicleInsuranceUpsertRequest r) {
        HiredVehicleInsurance existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleInsurance", id.toString()));
        if (isExpired(existing.policyExpiryDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired vehicle insurance is view-only");
        }
        HiredVehicleInsuranceUpsertRequest merged = merge(existing, r);
        HiredVehicleInsuranceUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForHiredVehicle(updated.hiredVehicleId(), updated.insuranceId());
                    }
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicleInsurance", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("HiredVehicleInsurance", id.toString());
    }

    private HiredVehicleInsuranceUpsertRequest prepareForCreate(HiredVehicleInsuranceUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.hiredVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hiredVehicleId is required");

        UUID supplierId = r.supplierId() != null ? r.supplierId() : repository.findSupplierIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByHiredVehicleId(r.hiredVehicleId()).orElse(null);

        return new HiredVehicleInsuranceUpsertRequest(
                vehicleId,
                supplierId,
                r.hiredVehicleId(),
                r.insuranceCompany(),
                r.policyNumber(),
                r.insuranceTypeId(),
                r.policyStartDate(),
                r.policyExpiryDate(),
                r.idvAmount(),
                r.premiumAmount(),
                r.paymentMode(),
                r.paymentDate(),
                r.agentName(),
                r.agentContact(),
                r.agentEmail(),
                r.nomineeName(),
                r.addOnCovers(),
                r.ncbPercent(),
                r.claimCount(),
                r.lastClaimDate(),
                r.lastClaimAmount(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 30,
                r.insuranceStatus() != null ? r.insuranceStatus() : "Active",
                r.notes(),
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private HiredVehicleInsuranceUpsertRequest prepareForUpdate(HiredVehicleInsuranceUpsertRequest r) {
        return new HiredVehicleInsuranceUpsertRequest(
                r.vehicleId(),
                r.supplierId(),
                r.hiredVehicleId(),
                r.insuranceCompany(),
                r.policyNumber(),
                r.insuranceTypeId(),
                r.policyStartDate(),
                r.policyExpiryDate(),
                r.idvAmount(),
                r.premiumAmount(),
                r.paymentMode(),
                r.paymentDate(),
                r.agentName(),
                r.agentContact(),
                r.agentEmail(),
                r.nomineeName(),
                r.addOnCovers(),
                r.ncbPercent(),
                r.claimCount(),
                r.lastClaimDate(),
                r.lastClaimAmount(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : 30,
                r.insuranceStatus() != null ? r.insuranceStatus() : "Active",
                r.notes(),
                r.isCurrent() != null ? r.isCurrent() : Boolean.TRUE
        );
    }

    private void validate(HiredVehicleInsuranceUpsertRequest r) {
        if (r.hiredVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "hiredVehicleId is required");
        if (r.supplierId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "supplierId is required");
        if (!repository.HiredVehicleBelongsToCompany(r.hiredVehicleId(), r.supplierId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected hired vehicle does not belong to selected supplier");
        }
        if (r.insuranceCompany() == null || r.insuranceCompany().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "insuranceCompany is required");
        }
        if (r.policyNumber() == null || r.policyNumber().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "policyNumber is required");
        }
        if (r.policyStartDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "policyStartDate is required");
        }
        if (r.policyExpiryDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "policyExpiryDate is required");
        }
        if (r.insuranceStatus() != null) {
            String s = r.insuranceStatus().trim();
            if (!s.equals("Active") && !s.equals("Expired") && !s.equals("Cancelled")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "insuranceStatus must be Active, Expired or Cancelled");
            }
        }
    }

    private HiredVehicleInsuranceUpsertRequest merge(HiredVehicleInsurance c, HiredVehicleInsuranceUpsertRequest r) {
        return new HiredVehicleInsuranceUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.supplierId() != null ? r.supplierId() : c.supplierId(),
                r.hiredVehicleId() != null ? r.hiredVehicleId() : c.hiredVehicleId(),
                r.insuranceCompany() != null ? r.insuranceCompany() : c.insuranceCompany(),
                r.policyNumber() != null ? r.policyNumber() : c.policyNumber(),
                r.insuranceTypeId() != null ? r.insuranceTypeId() : c.insuranceTypeId(),
                r.policyStartDate() != null ? r.policyStartDate() : c.policyStartDate(),
                r.policyExpiryDate() != null ? r.policyExpiryDate() : c.policyExpiryDate(),
                r.idvAmount() != null ? r.idvAmount() : c.idvAmount(),
                r.premiumAmount() != null ? r.premiumAmount() : c.premiumAmount(),
                r.paymentMode() != null ? r.paymentMode() : c.paymentMode(),
                r.paymentDate() != null ? r.paymentDate() : c.paymentDate(),
                r.agentName() != null ? r.agentName() : c.agentName(),
                r.agentContact() != null ? r.agentContact() : c.agentContact(),
                r.agentEmail() != null ? r.agentEmail() : c.agentEmail(),
                r.nomineeName() != null ? r.nomineeName() : c.nomineeName(),
                r.addOnCovers() != null ? r.addOnCovers() : c.addOnCovers(),
                r.ncbPercent() != null ? r.ncbPercent() : c.ncbPercent(),
                r.claimCount() != null ? r.claimCount() : c.claimCount(),
                r.lastClaimDate() != null ? r.lastClaimDate() : c.lastClaimDate(),
                r.lastClaimAmount() != null ? r.lastClaimAmount() : c.lastClaimAmount(),
                r.renewalReminderDays() != null ? r.renewalReminderDays() : c.renewalReminderDays(),
                r.insuranceStatus() != null ? r.insuranceStatus() : c.insuranceStatus(),
                r.notes() != null ? r.notes() : c.notes(),
                r.isCurrent() != null ? r.isCurrent() : c.isCurrent()
        );
    }

    private boolean isExpired(LocalDate expiryDate) {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }
}



