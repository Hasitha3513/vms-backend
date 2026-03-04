package genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsurancePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.service.VehicleInsuranceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/vehicle-insurances")
public class VehicleInsuranceController {
    private final VehicleInsuranceService service;

    public VehicleInsuranceController(VehicleInsuranceService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleInsuranceResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID companyVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId, companyVehicleId), filters, Set.of("companyId", "companyVehicleId"));
    }

    @GetMapping("/prefill/company-vehicles/{companyVehicleId}")
    public VehicleInsurancePrefillResponse prefillByCompanyVehicle(@PathVariable UUID companyVehicleId) {
        return service.getPrefillByCompanyVehicle(companyVehicleId);
    }

    @GetMapping("/supplier-options")
    public List<VehicleInsuranceSupplierOptionResponse> supplierOptions(
            @RequestParam(required = false) UUID companyId
    ) {
        return service.supplierOptions(companyId);
    }

    @GetMapping("/{id}")
    public VehicleInsuranceResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleInsuranceResponse create(@RequestBody VehicleInsuranceUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public VehicleInsuranceResponse update(@PathVariable UUID id, @RequestBody VehicleInsuranceUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
