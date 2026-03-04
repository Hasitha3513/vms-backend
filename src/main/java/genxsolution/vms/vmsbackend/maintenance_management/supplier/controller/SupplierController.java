package genxsolution.vms.vmsbackend.maintenance_management.supplier.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierDetailsResponse;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierOverviewResponse;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.dto.SupplierUpsertRequest;
import genxsolution.vms.vmsbackend.maintenance_management.supplier.service.SupplierService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/suppliers")
public class SupplierController {
    private final SupplierService service;

    public SupplierController(SupplierService service) {
        this.service = service;
    }

    @GetMapping("/overview")
    public SupplierOverviewResponse overview() {
        return service.overview();
    }

    @GetMapping("/details")
    public List<SupplierDetailsResponse> details(@RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.details(), filters, Set.of());
    }

    @GetMapping("/{id}")
    public SupplierDetailsResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierDetailsResponse create(@RequestBody SupplierUpsertRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public SupplierDetailsResponse update(@PathVariable UUID id, @RequestBody SupplierUpsertRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
