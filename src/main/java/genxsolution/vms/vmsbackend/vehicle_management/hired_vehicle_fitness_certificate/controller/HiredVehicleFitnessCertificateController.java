package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificatePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificateResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.dto.HiredVehicleFitnessCertificateUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_fitness_certificate.service.HiredVehicleFitnessCertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/hired-vehicle-fitness-certificates")
public class HiredVehicleFitnessCertificateController {
    private final HiredVehicleFitnessCertificateService service;

    public HiredVehicleFitnessCertificateController(HiredVehicleFitnessCertificateService service) {
        this.service = service;
    }

    @GetMapping
    public List<HiredVehicleFitnessCertificateResponse> list(
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) UUID hiredVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(supplierId, hiredVehicleId), filters, Set.of("supplierId", "hiredVehicleId"));
    }

    @GetMapping("/prefill/hired-vehicles/{hiredVehicleId}")
    public HiredVehicleFitnessCertificatePrefillResponse prefillByHiredVehicle(@PathVariable UUID hiredVehicleId) {
        return service.getPrefillByHiredVehicle(hiredVehicleId);
    }

    @GetMapping("/{id}")
    public HiredVehicleFitnessCertificateResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HiredVehicleFitnessCertificateResponse create(@RequestBody HiredVehicleFitnessCertificateUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public HiredVehicleFitnessCertificateResponse update(@PathVariable UUID id, @RequestBody HiredVehicleFitnessCertificateUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}



