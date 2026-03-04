package genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsurancePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.mapper.VehicleInsuranceMapper;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.model.VehicleInsurance;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.repository.VehicleInsuranceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class VehicleInsuranceService {
    private final VehicleInsuranceRepository repository;
    private final VehicleInsuranceMapper mapper;

    public VehicleInsuranceService(VehicleInsuranceRepository repository, VehicleInsuranceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<VehicleInsuranceResponse> list(UUID companyId, UUID companyVehicleId) {
        return repository.findAll(companyId, companyVehicleId).stream().map(mapper::toResponse).toList();
    }

    public VehicleInsuranceResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleInsurance", id.toString()));
    }

    public List<VehicleInsuranceSupplierOptionResponse> supplierOptions(UUID companyId) {
        return repository.findInsuranceSupplierOptions(companyId);
    }

    public VehicleInsurancePrefillResponse getPrefillByCompanyVehicle(UUID companyVehicleId) {
        UUID vehicleId = repository.findVehicleIdByCompanyVehicleId(companyVehicleId).orElse(null);
        UUID companyId = repository.findCompanyIdByCompanyVehicleId(companyVehicleId).orElse(null);
        VehicleInsurance latest = repository.findLatestByCompanyVehicleId(companyVehicleId).orElse(null);

        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyVehicleId");
        }

        return new VehicleInsurancePrefillResponse(
                vehicleId,
                companyId,
                companyVehicleId,
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

    public VehicleInsuranceResponse create(VehicleInsuranceUpsertRequest r) {
        VehicleInsuranceUpsertRequest prepared = prepareForCreate(r);
        validate(prepared);
        VehicleInsurance created = repository.create(prepared);
        if (Boolean.TRUE.equals(created.isCurrent())) {
            repository.clearCurrentForCompanyVehicle(created.companyVehicleId(), created.insuranceId());
        }
        return mapper.toResponse(created);
    }

    public VehicleInsuranceResponse update(UUID id, VehicleInsuranceUpsertRequest r) {
        VehicleInsurance existing = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleInsurance", id.toString()));
        if (isExpired(existing.policyExpiryDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Expired vehicle insurance is view-only");
        }
        VehicleInsuranceUpsertRequest merged = merge(existing, r);
        VehicleInsuranceUpsertRequest prepared = prepareForUpdate(merged);
        validate(prepared);
        return repository.update(id, prepared).map(updated -> {
                    if (Boolean.TRUE.equals(updated.isCurrent())) {
                        repository.clearCurrentForCompanyVehicle(updated.companyVehicleId(), updated.insuranceId());
                    }
                    return mapper.toResponse(updated);
                })
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleInsurance", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("VehicleInsurance", id.toString());
    }

    private VehicleInsuranceUpsertRequest prepareForCreate(VehicleInsuranceUpsertRequest r) {
        if (r == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request body is required");
        if (r.companyVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyVehicleId is required");

        UUID companyId = r.companyId() != null ? r.companyId() : repository.findCompanyIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);
        UUID vehicleId = r.vehicleId() != null ? r.vehicleId() : repository.findVehicleIdByCompanyVehicleId(r.companyVehicleId()).orElse(null);

        return new VehicleInsuranceUpsertRequest(
                vehicleId,
                companyId,
                r.companyVehicleId(),
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

    private VehicleInsuranceUpsertRequest prepareForUpdate(VehicleInsuranceUpsertRequest r) {
        return new VehicleInsuranceUpsertRequest(
                r.vehicleId(),
                r.companyId(),
                r.companyVehicleId(),
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

    private void validate(VehicleInsuranceUpsertRequest r) {
        if (r.companyVehicleId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyVehicleId is required");
        if (r.companyId() == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required");
        if (!repository.companyVehicleBelongsToCompany(r.companyVehicleId(), r.companyId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected company vehicle does not belong to selected company");
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

    private VehicleInsuranceUpsertRequest merge(VehicleInsurance c, VehicleInsuranceUpsertRequest r) {
        return new VehicleInsuranceUpsertRequest(
                r.vehicleId() != null ? r.vehicleId() : c.vehicleId(),
                r.companyId() != null ? r.companyId() : c.companyId(),
                r.companyVehicleId() != null ? r.companyVehicleId() : c.companyVehicleId(),
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
