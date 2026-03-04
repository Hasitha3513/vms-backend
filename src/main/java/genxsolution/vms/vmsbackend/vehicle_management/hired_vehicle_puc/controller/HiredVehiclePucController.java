package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.dto.HiredVehiclePucUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_puc.service.HiredVehiclePucService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/hired-vehicle-pucs")
public class HiredVehiclePucController {
    private final HiredVehiclePucService service;

    public HiredVehiclePucController(HiredVehiclePucService service) {
        this.service = service;
    }

    @GetMapping
    public List<HiredVehiclePucResponse> list(
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) UUID hiredVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(supplierId, hiredVehicleId), filters, Set.of("supplierId", "hiredVehicleId"));
    }

    @GetMapping("/prefill/hired-vehicles/{hiredVehicleId}")
    public HiredVehiclePucPrefillResponse prefillByHiredVehicle(@PathVariable UUID hiredVehicleId) {
        return service.getPrefillByHiredVehicle(hiredVehicleId);
    }

    @GetMapping("/{id}")
    public HiredVehiclePucResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HiredVehiclePucResponse create(@RequestBody HiredVehiclePucUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public HiredVehiclePucResponse update(@PathVariable UUID id, @RequestBody HiredVehiclePucUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}



