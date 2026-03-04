package genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificatePrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificateResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.dto.VehicleFitnessCertificateUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_fitness_certificate.service.VehicleFitnessCertificateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/vehicle-fitness-certificates")
public class VehicleFitnessCertificateController {
    private final VehicleFitnessCertificateService service;

    public VehicleFitnessCertificateController(VehicleFitnessCertificateService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleFitnessCertificateResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID companyVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId, companyVehicleId), filters, Set.of("companyId", "companyVehicleId"));
    }

    @GetMapping("/prefill/company-vehicles/{companyVehicleId}")
    public VehicleFitnessCertificatePrefillResponse prefillByCompanyVehicle(@PathVariable UUID companyVehicleId) {
        return service.getPrefillByCompanyVehicle(companyVehicleId);
    }

    @GetMapping("/{id}")
    public VehicleFitnessCertificateResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleFitnessCertificateResponse create(@RequestBody VehicleFitnessCertificateUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public VehicleFitnessCertificateResponse update(@PathVariable UUID id, @RequestBody VehicleFitnessCertificateUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
