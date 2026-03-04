package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.service;

import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleOverviewResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleCurrentOwnershipOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleOwnershipTypeOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleTypeCountResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.mapper.CompanyVehicleMapper;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.model.CompanyVehicle;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.repository.CompanyVehicleRepository;
import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CompanyVehicleService {
    private final CompanyVehicleRepository repository;
    private final CompanyVehicleMapper mapper;

    public CompanyVehicleService(CompanyVehicleRepository repository, CompanyVehicleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<CompanyVehicleResponse> list(UUID companyId) {
        return repository.findAll(companyId).stream().map(mapper::toResponse).toList();
    }

    public CompanyVehicleResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("CompanyVehicle", id.toString()));
    }

    public CompanyVehicleResponse getFirst(UUID companyId) {
        return repository.findFirst(companyId).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("CompanyVehicle", "first"));
    }

    public CompanyVehicleOverviewResponse overview(UUID companyId) {
        long[] values = repository.overview(companyId);
        List<CompanyVehicleTypeCountResponse> byType = repository.overviewByType(companyId);
        if (values == null || values.length < 8) {
            return new CompanyVehicleOverviewResponse(0, 0, 0, 0, 0, 0, 0, 0, byType);
        }
        return new CompanyVehicleOverviewResponse(
                values[0],
                values[1],
                values[2],
                values[3],
                values[4],
                values[5],
                values[6],
                values[7],
                byType
        );
    }

    public List<CompanyVehicleOwnershipTypeOptionResponse> ownershipTypeOptions() {
        return repository.findOwnedAndLeasedOwnershipTypes();
    }

    public List<CompanyVehicleCurrentOwnershipOptionResponse> currentOwnershipOptions(UUID companyId, Integer ownershipTypeId) {
        if (ownershipTypeId == null) {
            return repository.findLeasedCurrentOwnershipOptions(companyId);
        }
        String ownershipTypeCode = repository.findOwnershipTypeCodeById(ownershipTypeId)
                .map(this::normalizeOwnershipTypeCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Ownership Type"));

        if (isCompanyOwnedCode(ownershipTypeCode)) {
            if (companyId == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required for owned vehicles");
            }
            return repository.findOwnedCurrentOwnershipOption(companyId)
                    .map(List::of)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyId"));
        }
        if (isPersonalOwnedCode(ownershipTypeCode)) {
            return List.of();
        }
        if (isLeasedCode(ownershipTypeCode)) {
            List<CompanyVehicleCurrentOwnershipOptionResponse> options = repository.findLeasedCurrentOwnershipOptions(companyId);
            if (!options.isEmpty() || companyId == null) {
                return options;
            }
            // Fallback for legacy/misaligned supplier tenant mapping.
            return repository.findLeasedCurrentOwnershipOptions(null);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ownership Type must be Company Owned, Personal Owned, or Leased");
    }

    public CompanyVehicleResponse create(CompanyVehicleUpsertRequest r) {
        CompanyVehicleUpsertRequest prepared = prepareRequestForCreate(r);
        prepared = applyOwnershipAndDefaults(prepared);
        validateRelations(prepared);
        return mapper.toResponse(repository.create(prepared));
    }

    public CompanyVehicleResponse update(UUID id, CompanyVehicleUpsertRequest r) {
        CompanyVehicle current = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("CompanyVehicle", id.toString()));
        CompanyVehicleUpsertRequest merged = merge(current, r);
        CompanyVehicleUpsertRequest prepared = prepareRequestForUpdate(current, merged, r);
        prepared = applyOwnershipAndDefaults(prepared);
        validateRelations(prepared);
        return repository.update(id, prepared).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("CompanyVehicle", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("CompanyVehicle", id.toString());
    }

    public String nextIdentificationCode(UUID companyId, UUID typeId) {
        if (companyId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required");
        if (typeId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "typeId is required");
        String typeIdentifier = repository.findCompanyTypeIdentifierCode(companyId, typeId)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No identity type code configured for selected company and vehicle type"
                ));
        String prefix = typeIdentifier.toUpperCase();
        Pattern pattern = Pattern.compile("^" + Pattern.quote(prefix) + "\\s*(\\d+)$", Pattern.CASE_INSENSITIVE);
        int max = 0;
        for (String code : repository.findIdentificationCodesByCompanyAndType(companyId, typeId)) {
            if (code == null) continue;
            Matcher m = pattern.matcher(code.trim());
            if (m.matches()) {
                try {
                    max = Math.max(max, Integer.parseInt(m.group(1)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return prefix + " " + String.format("%03d", max + 1);
    }

    private CompanyVehicleUpsertRequest prepareRequestForCreate(CompanyVehicleUpsertRequest r) {
        if (r.companyId() != null && r.companyVehicleType() != null) {
            String generated = nextIdentificationCode(r.companyId(), r.companyVehicleType());
            return withKeyNumber(r, generated);
        }
        return r;
    }

    private CompanyVehicleUpsertRequest prepareRequestForUpdate(
            CompanyVehicle current,
            CompanyVehicleUpsertRequest merged,
            CompanyVehicleUpsertRequest incoming
    ) {
        if (merged.companyId() == null || merged.companyVehicleType() == null) {
            return merged;
        }

        boolean companyChanged = !merged.companyId().equals(current.companyId());
        boolean typeChanged = !merged.companyVehicleType().equals(current.companyVehicleType());
        boolean requestedBlankKey = incoming.keyNumber() != null && incoming.keyNumber().trim().isEmpty();
        boolean missingKey = merged.keyNumber() == null || merged.keyNumber().trim().isEmpty();

        if (companyChanged || typeChanged || requestedBlankKey || missingKey) {
            String generated = nextIdentificationCode(merged.companyId(), merged.companyVehicleType());
            return withKeyNumber(merged, generated);
        }
        return merged;
    }

    private CompanyVehicleUpsertRequest withKeyNumber(CompanyVehicleUpsertRequest r, String keyNumber) {
        return new CompanyVehicleUpsertRequest(
                r.companyId(),
                r.companyCode(),
                r.companyProject(),
                r.companyBranch(),
                r.companyDepartment(),
                r.companyVehicleType(),
                r.companyVehicleModel(),
                r.categoryId(),
                r.companyVehicleManufacture(),
                r.registrationNumber(),
                r.chassisNumber(),
                r.engineNumber(),
                keyNumber,
                r.vehicleImage(),
                r.manufactureYear(),
                r.color(),
                r.fuelTypeId(),
                r.transmissionTypeId(),
                r.numberPlateTypeId(),
                r.bodyStyleId(),
                r.seatingCapacity(),
                r.undercarriageTypeId(),
                r.engineTypeId(),
                r.engineManufactureId(),
                r.initialOdometerKm(),
                r.currentOdometerKm(),
                r.totalEngineHours(),
                r.consumptionMethodId(),
                r.ratedEfficiencyKmpl(),
                r.ratedConsumptionLph(),
                r.ownershipTypeId(),
                r.currentOwnership(),
                r.previousOwnersCount(),
                r.manufactureId(),
                r.distributorId(),
                r.vehicleCondition(),
                r.operationalStatusId(),
                r.vehicleStatusId(),
                r.notes(),
                r.isActive(),
                r.createdBy(),
                r.updatedBy()
        );
    }

    private CompanyVehicleUpsertRequest applyOwnershipAndDefaults(CompanyVehicleUpsertRequest r) {
        Integer year = r.manufactureYear() != null ? r.manufactureYear() : Year.now().getValue();
        Integer ownershipTypeId = resolveOwnedOrLeasedOwnershipTypeId(r.ownershipTypeId());
        String ownershipTypeCode = repository.findOwnershipTypeCodeById(ownershipTypeId)
                .map(this::normalizeOwnershipTypeCode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Ownership Type"));

        if (!isCompanyOwnedCode(ownershipTypeCode) && !isPersonalOwnedCode(ownershipTypeCode) && !isLeasedCode(ownershipTypeCode)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ownership Type must be Company Owned, Personal Owned, or Leased");
        }

        String currentOwnership;
        if (isCompanyOwnedCode(ownershipTypeCode)) {
            if (r.companyId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required for owned vehicles");
            }
            currentOwnership = repository.findCompanyNameById(r.companyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyId"));
        } else if (isPersonalOwnedCode(ownershipTypeCode)) {
            currentOwnership = trimToNull(r.currentOwnership());
            if (currentOwnership == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ownership is required for personal owned vehicles");
            }
        } else {
            currentOwnership = trimToNull(r.currentOwnership());
            if (currentOwnership == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current Ownership is required for leased vehicles");
            }
            boolean exists = repository.leasedCurrentOwnershipExists(r.companyId(), currentOwnership)
                    || (r.companyId() != null && repository.leasedCurrentOwnershipExists(null, currentOwnership));
            if (!exists) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Current Ownership must be a Finance/Leasing Provider supplier from dropdown"
                );
            }
        }

        return new CompanyVehicleUpsertRequest(
                r.companyId(),
                r.companyCode(),
                r.companyProject(),
                r.companyBranch(),
                r.companyDepartment(),
                r.companyVehicleType(),
                r.companyVehicleModel(),
                r.categoryId(),
                r.companyVehicleManufacture(),
                r.registrationNumber(),
                r.chassisNumber(),
                r.engineNumber(),
                r.keyNumber(),
                r.vehicleImage(),
                year,
                r.color(),
                r.fuelTypeId(),
                r.transmissionTypeId(),
                r.numberPlateTypeId(),
                r.bodyStyleId(),
                r.seatingCapacity(),
                r.undercarriageTypeId(),
                r.engineTypeId(),
                r.engineManufactureId(),
                r.initialOdometerKm(),
                r.currentOdometerKm(),
                r.totalEngineHours(),
                r.consumptionMethodId(),
                r.ratedEfficiencyKmpl(),
                r.ratedConsumptionLph(),
                ownershipTypeId,
                currentOwnership,
                r.previousOwnersCount(),
                r.manufactureId(),
                r.distributorId(),
                r.vehicleCondition(),
                r.operationalStatusId(),
                r.vehicleStatusId(),
                r.notes(),
                r.isActive(),
                r.createdBy(),
                r.updatedBy()
        );
    }

    private void validateRelations(CompanyVehicleUpsertRequest r) {
        if (r.companyId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required");
        }

        UUID projectBranch = null;
        if (r.companyProject() != null) {
            projectBranch = repository.findProjectBranchForCompany(r.companyProject(), r.companyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project does not belong to selected company"));
        }

        if (r.companyBranch() != null && projectBranch != null && !r.companyBranch().equals(projectBranch)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Branch must match selected project's branch");
        }

        if (r.companyDepartment() != null) {
            if (!repository.departmentBelongsToCompany(r.companyDepartment(), r.companyId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department does not belong to selected company");
            }
            UUID branchToCheck = projectBranch != null ? projectBranch : r.companyBranch();
            if (branchToCheck != null && !repository.departmentBelongsToBranch(r.companyDepartment(), branchToCheck)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Department does not belong to selected project/branch");
            }
        }
    }

    private CompanyVehicleUpsertRequest merge(CompanyVehicle c, CompanyVehicleUpsertRequest r) {
        return new CompanyVehicleUpsertRequest(
                r.companyId() != null ? r.companyId() : c.companyId(),
                r.companyCode() != null ? r.companyCode() : c.companyCode(),
                r.companyProject() != null ? r.companyProject() : c.companyProject(),
                r.companyBranch() != null ? r.companyBranch() : c.companyBranch(),
                r.companyDepartment() != null ? r.companyDepartment() : c.companyDepartment(),
                r.companyVehicleType() != null ? r.companyVehicleType() : c.companyVehicleType(),
                r.companyVehicleModel() != null ? r.companyVehicleModel() : c.companyVehicleModel(),
                r.categoryId() != null ? r.categoryId() : c.categoryId(),
                r.companyVehicleManufacture() != null ? r.companyVehicleManufacture() : c.companyVehicleManufacture(),
                r.registrationNumber() != null ? r.registrationNumber() : c.registrationNumber(),
                r.chassisNumber() != null ? r.chassisNumber() : c.chassisNumber(),
                r.engineNumber() != null ? r.engineNumber() : c.engineNumber(),
                r.keyNumber() != null ? r.keyNumber() : c.keyNumber(),
                r.vehicleImage() != null ? r.vehicleImage() : c.vehicleImage(),
                r.manufactureYear() != null ? r.manufactureYear() : c.manufactureYear(),
                r.color() != null ? r.color() : c.color(),
                r.fuelTypeId() != null ? r.fuelTypeId() : c.fuelTypeId(),
                r.transmissionTypeId() != null ? r.transmissionTypeId() : c.transmissionTypeId(),
                r.numberPlateTypeId() != null ? r.numberPlateTypeId() : c.numberPlateTypeId(),
                r.bodyStyleId() != null ? r.bodyStyleId() : c.bodyStyleId(),
                r.seatingCapacity() != null ? r.seatingCapacity() : c.seatingCapacity(),
                r.undercarriageTypeId() != null ? r.undercarriageTypeId() : c.undercarriageTypeId(),
                r.engineTypeId() != null ? r.engineTypeId() : c.engineTypeId(),
                r.engineManufactureId() != null ? r.engineManufactureId() : c.engineManufactureId(),
                r.initialOdometerKm() != null ? r.initialOdometerKm() : c.initialOdometerKm(),
                r.currentOdometerKm() != null ? r.currentOdometerKm() : c.currentOdometerKm(),
                r.totalEngineHours() != null ? r.totalEngineHours() : c.totalEngineHours(),
                r.consumptionMethodId() != null ? r.consumptionMethodId() : c.consumptionMethodId(),
                r.ratedEfficiencyKmpl() != null ? r.ratedEfficiencyKmpl() : c.ratedEfficiencyKmpl(),
                r.ratedConsumptionLph() != null ? r.ratedConsumptionLph() : c.ratedConsumptionLph(),
                r.ownershipTypeId() != null ? r.ownershipTypeId() : c.ownershipTypeId(),
                r.currentOwnership() != null ? r.currentOwnership() : c.currentOwnership(),
                r.previousOwnersCount() != null ? r.previousOwnersCount() : c.previousOwnersCount(),
                r.manufactureId() != null ? r.manufactureId() : c.manufactureId(),
                r.distributorId() != null ? r.distributorId() : c.distributorId(),
                r.vehicleCondition() != null ? r.vehicleCondition() : c.vehicleCondition(),
                r.operationalStatusId() != null ? r.operationalStatusId() : c.operationalStatusId(),
                r.vehicleStatusId() != null ? r.vehicleStatusId() : c.vehicleStatusId(),
                r.notes() != null ? r.notes() : c.notes(),
                r.isActive() != null ? r.isActive() : c.isActive(),
                r.createdBy() != null ? r.createdBy() : c.createdBy(),
                r.updatedBy() != null ? r.updatedBy() : c.updatedBy()
        );
    }

    private Integer resolveOwnedOrLeasedOwnershipTypeId(Integer requestedOwnershipTypeId) {
        if (requestedOwnershipTypeId != null) {
            return requestedOwnershipTypeId;
        }
        return repository.findOwnershipTypeIdByCode("COMPANY OWNED")
                .or(() -> repository.findOwnershipTypeIdByCode("OWNED"))
                .or(() -> repository.findOwnershipTypeIdByCode("PERSONAL OWNED"))
                .or(() -> repository.findOwnershipTypeIdByCode("LEASED"))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Ownership Type must include Company Owned, Personal Owned, or Leased setup"
                ));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String normalizeOwnershipTypeCode(String code) {
        if (code == null) {
            return "";
        }
        return code.toUpperCase(Locale.ROOT).replace(" ", "").replace("_", "").replace("-", "");
    }

    private boolean isCompanyOwnedCode(String normalizedCode) {
        return "OWNED".equals(normalizedCode) || "COMPANYOWNED".equals(normalizedCode);
    }

    private boolean isPersonalOwnedCode(String normalizedCode) {
        return "PERSONALOWNED".equals(normalizedCode);
    }

    private boolean isLeasedCode(String normalizedCode) {
        return "LEASED".equals(normalizedCode);
    }
}
