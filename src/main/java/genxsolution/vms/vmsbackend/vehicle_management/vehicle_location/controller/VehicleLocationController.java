package genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.dto.VehicleLocationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.dto.VehicleLocationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.service.VehicleLocationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/locations")
public class VehicleLocationController {
    private final VehicleLocationService service;

    public VehicleLocationController(VehicleLocationService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleLocationResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId, vehicleId), filters, Set.of("companyId", "vehicleId"));
    }

    @GetMapping("/list")
    public List<VehicleLocationResponse> listAlias(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID vehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId, vehicleId), filters, Set.of("companyId", "vehicleId"));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public VehicleLocationResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleLocationResponse create(@RequestBody VehicleLocationUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public VehicleLocationResponse update(@PathVariable UUID id, @RequestBody VehicleLocationUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
