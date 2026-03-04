package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsurancePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsuranceResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsuranceSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsuranceUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.service.HiredVehicleInsuranceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/hired-vehicle-insurances")
public class HiredVehicleInsuranceController {
    private final HiredVehicleInsuranceService service;

    public HiredVehicleInsuranceController(HiredVehicleInsuranceService service) {
        this.service = service;
    }

    @GetMapping
    public List<HiredVehicleInsuranceResponse> list(
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) UUID hiredVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(supplierId, hiredVehicleId), filters, Set.of("supplierId", "hiredVehicleId"));
    }

    @GetMapping("/prefill/hired-vehicles/{hiredVehicleId}")
    public HiredVehicleInsurancePrefillResponse prefillByHiredVehicle(@PathVariable UUID hiredVehicleId) {
        return service.getPrefillByHiredVehicle(hiredVehicleId);
    }

    @GetMapping("/supplier-options")
    public List<HiredVehicleInsuranceSupplierOptionResponse> supplierOptions(
            @RequestParam(required = false) UUID supplierId
    ) {
        return service.supplierOptions(supplierId);
    }

    @GetMapping("/{id}")
    public HiredVehicleInsuranceResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HiredVehicleInsuranceResponse create(@RequestBody HiredVehicleInsuranceUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public HiredVehicleInsuranceResponse update(@PathVariable UUID id, @RequestBody HiredVehicleInsuranceUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}



