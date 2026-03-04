package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto.VehicleModelVariantResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto.VehicleModelVariantUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.service.VehicleModelVariantService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/model-variants")
public class VehicleModelVariantController {
    private final VehicleModelVariantService service;

    public VehicleModelVariantController(VehicleModelVariantService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<VehicleModelVariantResponse> list(@RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(), filters, Set.of());
    }

    @GetMapping("/{id}")
    public VehicleModelVariantResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleModelVariantResponse create(@RequestBody VehicleModelVariantUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public VehicleModelVariantResponse update(@PathVariable UUID id, @RequestBody VehicleModelVariantUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
