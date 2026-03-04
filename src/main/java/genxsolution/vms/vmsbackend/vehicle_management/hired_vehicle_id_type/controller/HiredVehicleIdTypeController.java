package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto.HiredVehicleIdTypeResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.dto.HiredVehicleIdTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_id_type.service.HiredVehicleIdTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/hired-vehicle-id-types")
public class HiredVehicleIdTypeController {
    private final HiredVehicleIdTypeService service;

    public HiredVehicleIdTypeController(HiredVehicleIdTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<HiredVehicleIdTypeResponse> list(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId"));
    }

    @GetMapping("/{id}")
    public HiredVehicleIdTypeResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HiredVehicleIdTypeResponse create(@RequestBody HiredVehicleIdTypeUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public HiredVehicleIdTypeResponse update(@PathVariable UUID id, @RequestBody HiredVehicleIdTypeUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
