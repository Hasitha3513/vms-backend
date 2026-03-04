package genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.dto.VehicleRegistrationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_registration.service.VehicleRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/vehicle-registrations")
public class VehicleRegistrationController {
    private final VehicleRegistrationService service;

    public VehicleRegistrationController(VehicleRegistrationService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleRegistrationResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID companyVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId, companyVehicleId), filters, Set.of("companyId", "companyVehicleId"));
    }

    @GetMapping("/company-vehicles/{companyVehicleId}")
    public List<VehicleRegistrationResponse> listByCompanyVehicle(
            @PathVariable UUID companyVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(null, companyVehicleId), filters, Set.of("companyVehicleId"));
    }

    @GetMapping("/prefill/company-vehicles/{companyVehicleId}")
    public VehicleRegistrationPrefillResponse prefillByCompanyVehicle(@PathVariable UUID companyVehicleId) {
        return service.getPrefillByCompanyVehicle(companyVehicleId);
    }

    @GetMapping("/{id}")
    public VehicleRegistrationResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleRegistrationResponse create(@RequestBody VehicleRegistrationUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public VehicleRegistrationResponse update(@PathVariable UUID id, @RequestBody VehicleRegistrationUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
