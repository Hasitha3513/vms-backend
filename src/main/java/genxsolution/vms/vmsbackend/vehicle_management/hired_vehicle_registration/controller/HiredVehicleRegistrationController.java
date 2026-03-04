package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.dto.HiredVehicleRegistrationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_registration.service.HiredVehicleRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/hired-vehicle-registrations")
public class HiredVehicleRegistrationController {
    private final HiredVehicleRegistrationService service;

    public HiredVehicleRegistrationController(HiredVehicleRegistrationService service) {
        this.service = service;
    }

    @GetMapping
    public List<HiredVehicleRegistrationResponse> list(
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) UUID hiredVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(supplierId, hiredVehicleId), filters, Set.of("supplierId", "hiredVehicleId"));
    }

    @GetMapping("/hired-vehicles/{hiredVehicleId}")
    public List<HiredVehicleRegistrationResponse> listByHiredVehicle(
            @PathVariable UUID hiredVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(null, hiredVehicleId), filters, Set.of("hiredVehicleId"));
    }

    @GetMapping("/prefill/hired-vehicles/{hiredVehicleId}")
    public HiredVehicleRegistrationPrefillResponse prefillByHiredVehicle(@PathVariable UUID hiredVehicleId) {
        return service.getPrefillByHiredVehicle(hiredVehicleId);
    }

    @GetMapping("/{id}")
    public HiredVehicleRegistrationResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HiredVehicleRegistrationResponse create(@RequestBody HiredVehicleRegistrationUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public HiredVehicleRegistrationResponse update(@PathVariable UUID id, @RequestBody HiredVehicleRegistrationUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}



