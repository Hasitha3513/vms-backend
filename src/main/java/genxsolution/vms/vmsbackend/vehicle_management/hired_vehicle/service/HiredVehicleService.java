package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleOverviewResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleOwnershipTypeOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleTypeCountResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleModelPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.mapper.HiredVehicleMapper;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.model.HiredVehicle;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.repository.HiredVehicleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Year;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HiredVehicleService {
    private final HiredVehicleRepository repository;
    private final HiredVehicleMapper mapper;

    public HiredVehicleService(HiredVehicleRepository repository, HiredVehicleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<HiredVehicleResponse> list(UUID supplierId) {
        return repository.findAll(supplierId).stream().map(mapper::toResponse).toList();
    }

    public HiredVehicleResponse getById(UUID id) {
        return repository.findById(id).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicle", id.toString()));
    }

    public HiredVehicleResponse getFirst(UUID supplierId) {
        return repository.findFirst(supplierId).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicle", "first"));
    }

    public List<HiredVehicleResponse> registrationOptions(String query, Integer limit) {
        int effectiveLimit = limit == null ? 200 : limit;
        return repository.findByRegistrationNumber(query, effectiveLimit).stream()
                .map(mapper::toResponse)
                .toList();
    }

    public HiredVehicleOverviewResponse overview(UUID supplierId) {
        long[] values = repository.overview(supplierId);
        List<HiredVehicleTypeCountResponse> byType = repository.overviewByType(supplierId);
        if (values == null || values.length < 5) {
            return new HiredVehicleOverviewResponse(0, 0, 0, 0, 0, byType);
        }
        return new HiredVehicleOverviewResponse(values[0], values[1], values[2], values[3], values[4], byType);
    }

    public List<HiredVehicleSupplierOptionResponse> supplierOptions() {
        return repository.findHiredVehicleSupplierOptions();
    }

    public List<HiredVehicleOwnershipTypeOptionResponse> ownershipTypeOptions() {
        List<HiredVehicleOwnershipTypeOptionResponse> preferred = repository.findRentedAndThirdPartyOwnershipTypes();
        if (!preferred.isEmpty()) {
            return preferred;
        }
        return repository.findActiveOwnershipTypes();
    }

    public Optional<HiredVehicleModelPrefillResponse> modelPrefill(UUID modelId, UUID supplierId) {
        if (modelId == null) {
            return Optional.empty();
        }
        Optional<HiredVehicleModelPrefillResponse> prefill = repository.findModelPrefill(modelId, supplierId);
        if (prefill.isEmpty()) {
            UUID modelTypeId = repository.findTypeIdByModelId(modelId).orElse(null);
            if (modelTypeId != null) {
                prefill = repository.findTypePrefill(modelTypeId, supplierId);
            }
        }
        return prefill
                .map(this::normalizeModelPrefillOwnership);
    }

    public List<HiredVehicleSupplierOptionResponse> currentOwnershipOptions(UUID supplierId, Integer ownershipTypeId) {
        List<HiredVehicleSupplierOptionResponse> options = repository.findCurrentOwnershipSupplierOptions(supplierId);
        if (!options.isEmpty() || supplierId == null) {
            return options;
        }
        return repository.findCurrentOwnershipSupplierOptions(null);
    }

    public HiredVehicleResponse create(HiredVehicleUpsertRequest r) {
        HiredVehicleUpsertRequest prepared = prepareRequestForCreate(r);
        prepared = ensureManufactureYear(prepared);
        validateRelations(prepared);
        return mapper.toResponse(repository.create(prepared));
    }

    public HiredVehicleResponse update(UUID id, HiredVehicleUpsertRequest r) {
        HiredVehicle current = repository.findById(id)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicle", id.toString()));
        HiredVehicleUpsertRequest merged = merge(current, r);
        HiredVehicleUpsertRequest prepared = prepareRequestForUpdate(current, merged, r);
        prepared = ensureManufactureYear(prepared);
        validateRelations(prepared);
        return repository.update(id, prepared).map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("HiredVehicle", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("HiredVehicle", id.toString());
    }

    public String nextIdentificationCode(UUID supplierId, UUID typeId) {
        if (typeId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "typeId is required");
        String prefix;
        List<String> existingCodes;
        if (supplierId != null) {
            UUID companyId = repository.findCompanyIdBySupplierId(supplierId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected supplier has no company"));
            prefix = repository.findCompanyTypeIdentifierCode(companyId, typeId)
                    .or(() -> repository.findAnyTypeIdentifierCode(typeId))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "No identity type code configured for selected vehicle type"
                    ))
                    .toUpperCase();
            existingCodes = repository.findIdentificationCodesBySupplierAndType(supplierId, typeId);
        } else {
            prefix = repository.findAnyTypeIdentifierCode(typeId)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "No identity type code configured for selected vehicle type"
                    ))
                    .toUpperCase();
            existingCodes = repository.findIdentificationCodesByType(typeId);
        }
        Pattern pattern = Pattern.compile("^" + Pattern.quote(prefix) + "\\s*(\\d+)$", Pattern.CASE_INSENSITIVE);
        int max = 0;
        for (String code : existingCodes) {
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

    private HiredVehicleUpsertRequest prepareRequestForCreate(HiredVehicleUpsertRequest r) {
        HiredVehicleUpsertRequest withSupplier = ensureSupplierData(r);
        HiredVehicleUpsertRequest withType = ensureTypeFromModel(withSupplier);
        if (withType.supplierId() != null && withType.hiredVehicleType() != null) {
            String generated = nextIdentificationCode(withType.supplierId(), withType.hiredVehicleType());
            return withKeyNumber(withType, generated);
        }
        return withType;
    }

    private HiredVehicleUpsertRequest prepareRequestForUpdate(
            HiredVehicle current,
            HiredVehicleUpsertRequest merged,
            HiredVehicleUpsertRequest incoming
    ) {
        HiredVehicleUpsertRequest withSupplier = ensureSupplierData(merged);
        HiredVehicleUpsertRequest withType = ensureTypeFromModel(withSupplier);
        if (withType.supplierId() == null || withType.hiredVehicleType() == null) {
            return withType;
        }

        boolean supplierChanged = !withType.supplierId().equals(current.supplierId());
        boolean typeChanged = !withType.hiredVehicleType().equals(current.hiredVehicleType());
        boolean requestedBlankKey = incoming.keyNumber() != null && incoming.keyNumber().trim().isEmpty();
        boolean missingKey = withType.keyNumber() == null || withType.keyNumber().trim().isEmpty();

        if (supplierChanged || typeChanged || requestedBlankKey || missingKey) {
            String generated = nextIdentificationCode(withType.supplierId(), withType.hiredVehicleType());
            return withKeyNumber(withType, generated);
        }
        return withType;
    }

    private HiredVehicleUpsertRequest ensureSupplierData(HiredVehicleUpsertRequest r) {
        if (r.supplierId() == null) {
            return r;
        }
        String supplierCode = r.supplierCode();
        if (supplierCode == null || supplierCode.trim().isEmpty()) {
            supplierCode = repository.findSupplierCodeById(r.supplierId()).orElse(null);
        }
        return withSupplierCode(r, supplierCode);
    }

    private HiredVehicleUpsertRequest withSupplierCode(HiredVehicleUpsertRequest r, String supplierCode) {
        return new HiredVehicleUpsertRequest(
                r.supplierId(),
                supplierCode,
                r.hiredVehicleType(),
                r.hiredVehicleModel(),
                r.categoryId(),
                r.hiredVehicleManufacture(),
                r.registrationNumber(),
                r.chassisNumber(),
                r.engineNumber(),
                r.keyNumber(),
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

    private HiredVehicleUpsertRequest withKeyNumber(HiredVehicleUpsertRequest r, String keyNumber) {
        return new HiredVehicleUpsertRequest(
                r.supplierId(),
                r.supplierCode(),
                r.hiredVehicleType(),
                r.hiredVehicleModel(),
                r.categoryId(),
                r.hiredVehicleManufacture(),
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

    private HiredVehicleUpsertRequest ensureTypeFromModel(HiredVehicleUpsertRequest r) {
        if (r.hiredVehicleType() != null || r.hiredVehicleModel() == null) {
            return r;
        }
        UUID resolvedTypeId = repository.findTypeIdByModelId(r.hiredVehicleModel()).orElse(null);
        if (resolvedTypeId == null) {
            return r;
        }
        return new HiredVehicleUpsertRequest(
                r.supplierId(),
                r.supplierCode(),
                resolvedTypeId,
                r.hiredVehicleModel(),
                r.categoryId(),
                r.hiredVehicleManufacture(),
                r.registrationNumber(),
                r.chassisNumber(),
                r.engineNumber(),
                r.keyNumber(),
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

    private HiredVehicleUpsertRequest ensureManufactureYear(HiredVehicleUpsertRequest r) {
        Integer year = r.manufactureYear() != null ? r.manufactureYear() : Year.now().getValue();
        Integer ownershipTypeId = resolveAnyOwnershipTypeId(r.ownershipTypeId());
        repository.findOwnershipTypeCodeById(ownershipTypeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Ownership Type"));

        String currentOwnership = trimToNull(r.currentOwnership());
        if (currentOwnership == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current Ownership is required");
        }
        boolean exists = repository.currentOwnershipSupplierExists(r.supplierId(), currentOwnership)
                || (r.supplierId() != null && repository.currentOwnershipSupplierExists(null, currentOwnership));
        if (!exists) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Current Ownership must be selected from dropdown options"
            );
        }
        return new HiredVehicleUpsertRequest(
                r.supplierId(),
                r.supplierCode(),
                r.hiredVehicleType(),
                r.hiredVehicleModel(),
                r.categoryId(),
                r.hiredVehicleManufacture(),
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

    private void validateRelations(HiredVehicleUpsertRequest r) {
        if (r.supplierId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "supplierId is required");
        }
        if (!repository.supplierExists(r.supplierId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier does not exist");
        }
        if (!repository.supplierAllowedForHiring(r.supplierId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only Equipment Supplier, Rental Partner, or Transport Provider can be selected");
        }
    }

    private HiredVehicleUpsertRequest merge(HiredVehicle c, HiredVehicleUpsertRequest r) {
        return new HiredVehicleUpsertRequest(
                r.supplierId() != null ? r.supplierId() : c.supplierId(),
                r.supplierCode() != null ? r.supplierCode() : c.supplierCode(),
                r.hiredVehicleType() != null ? r.hiredVehicleType() : c.hiredVehicleType(),
                r.hiredVehicleModel() != null ? r.hiredVehicleModel() : c.hiredVehicleModel(),
                r.categoryId() != null ? r.categoryId() : c.categoryId(),
                r.hiredVehicleManufacture() != null ? r.hiredVehicleManufacture() : c.hiredVehicleManufacture(),
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

    private Integer resolveAnyOwnershipTypeId(Integer requestedOwnershipTypeId) {
        if (requestedOwnershipTypeId != null) {
            return requestedOwnershipTypeId;
        }
        return repository.findOwnershipTypeIdByCode("RENTED")
                .or(() -> repository.findOwnershipTypeIdByCode("THIRD_PARTY"))
                .or(() -> repository.findActiveOwnershipTypes().stream()
                        .map(HiredVehicleOwnershipTypeOptionResponse::typeId)
                        .findFirst())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Ownership Type setup is required"
                ));
    }

    private String trimToNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private HiredVehicleModelPrefillResponse normalizeModelPrefillOwnership(HiredVehicleModelPrefillResponse prefill) {
        Integer normalizedOwnershipTypeId = resolveHiredOwnershipTypeId(prefill.ownershipTypeId());
        return new HiredVehicleModelPrefillResponse(
                prefill.modelId(),
                prefill.typeId(),
                prefill.categoryId(),
                prefill.manufacturerId(),
                normalizedOwnershipTypeId,
                prefill.currentOwnership(),
                prefill.initialOdometerKm(),
                prefill.currentOdometerKm(),
                prefill.totalEngineHours(),
                prefill.consumptionMethodId(),
                prefill.ratedEfficiencyKmpl(),
                prefill.ratedConsumptionLph()
        );
    }

    private Integer resolveHiredOwnershipTypeId(Integer sourceOwnershipTypeId) {
        if (sourceOwnershipTypeId != null) {
            String sourceCode = repository.findOwnershipTypeCodeById(sourceOwnershipTypeId)
                    .map(this::normalizeOwnershipTypeCode)
                    .orElse("");
            if ("RENTED".equals(sourceCode) || "THIRDPARTY".equals(sourceCode)) {
                return sourceOwnershipTypeId;
            }
        }
        return repository.findOwnershipTypeIdByCode("RENTED")
                .or(() -> repository.findOwnershipTypeIdByCode("THIRD_PARTY"))
                .orElse(sourceOwnershipTypeId);
    }

    private String normalizeOwnershipTypeCode(String code) {
        if (code == null) {
            return "";
        }
        return code.toUpperCase(Locale.ROOT)
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "");
    }
}
