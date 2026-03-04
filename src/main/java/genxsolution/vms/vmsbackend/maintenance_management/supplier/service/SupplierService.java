package genxsolution.vms.vmsbackend.maintenance_management.supplier.service;

import genxsolution.vms.vmsbackend.authentication.AuthContext;
import genxsolution.vms.vmsbackend.authentication.AuthContextHolder;
import genxsolution.vms.vmsbackend.maintenance_management.exception.MaintenanceResourceNotFoundException;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierDetailsResponse;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierOverviewResponse;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierTypeCountResponse;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierUpsertRequest;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.repository.SupplierRepository;
import genxsolution.vms.vmsbackend.organization.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class SupplierService {
    private final SupplierRepository repository;
    private final CompanyRepository companyRepository;

    public SupplierService(SupplierRepository repository, CompanyRepository companyRepository) {
        this.repository = repository;
        this.companyRepository = companyRepository;
    }

    public SupplierOverviewResponse overview() {
        long[] values = repository.overview();
        List<SupplierTypeCountResponse> byType = repository.overviewByType();
        if (values == null || values.length < 4) {
            return new SupplierOverviewResponse(0, 0, 0, 0, byType);
        }
        return new SupplierOverviewResponse(values[0], values[1], values[2], values[3], byType);
    }

    public List<SupplierDetailsResponse> details() {
        return repository.details();
    }

    public SupplierDetailsResponse getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new MaintenanceResourceNotFoundException("Supplier", id.toString()));
    }

    public SupplierDetailsResponse create(SupplierUpsertRequest request) {
        SupplierUpsertRequest normalized = normalizeAndValidateRequest(request);
        return repository.create(normalized);
    }

    public SupplierDetailsResponse update(UUID id, SupplierUpsertRequest request) {
        SupplierUpsertRequest normalized = normalizeAndValidateRequest(request);
        return repository.update(id, normalized)
                .orElseThrow(() -> new MaintenanceResourceNotFoundException("Supplier", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) {
            throw new MaintenanceResourceNotFoundException("Supplier", id.toString());
        }
    }

    private SupplierUpsertRequest normalizeAndValidateRequest(SupplierUpsertRequest request) {
        if (request == null || request.supplierName() == null || request.supplierName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "supplierName is required");
        }

        UUID companyId = request.companyId();
        String companyCode = trimToNull(request.companyCode());

        AuthContext context = AuthContextHolder.get();
        String contextCompanyCode = context == null ? null : trimToNull(context.companyCode());

        if (companyId == null && companyCode == null) {
            companyCode = contextCompanyCode;
        }

        if (companyId == null && companyCode == null) {
            companyId = companyRepository.findAll(true).stream()
                    .map(company -> company.companyId())
                    .findFirst()
                    .orElse(null);
        }

        if (companyId == null && companyCode != null) {
            companyId = companyRepository.findByCode(companyCode)
                    .map(company -> company.companyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid companyCode"));
        }

        if (companyCode == null && companyId != null) {
            companyCode = companyRepository.findById(companyId)
                    .map(company -> company.companyCode())
                    .orElse(null);
        }

        return new SupplierUpsertRequest(
                companyId,
                companyCode,
                request.supplierName().trim(),
                trimToNull(request.contactName()),
                trimToNull(request.contactPerson()),
                trimToNull(request.phone()),
                trimToNull(request.email()),
                trimToNull(request.address()),
                trimToNull(request.taxId()),
                request.supplierTypeId(),
                request.isActive()
        );
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
