package genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.dto.VehiclePucUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_puc.service.VehiclePucService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/vehicle-pucs")
public class VehiclePucController {
    private final VehiclePucService service;

    public VehiclePucController(VehiclePucService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehiclePucResponse> list(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) UUID companyVehicleId,
            @RequestParam Map<String, String> filters
    ) {
        return ListQueryEngine.apply(service.list(companyId, companyVehicleId), filters, Set.of("companyId", "companyVehicleId"));
    }

    @GetMapping("/prefill/company-vehicles/{companyVehicleId}")
    public VehiclePucPrefillResponse prefillByCompanyVehicle(@PathVariable UUID companyVehicleId) {
        return service.getPrefillByCompanyVehicle(companyVehicleId);
    }

    @GetMapping("/{id}")
    public VehiclePucResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehiclePucResponse create(@RequestBody VehiclePucUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public VehiclePucResponse update(@PathVariable UUID id, @RequestBody VehiclePucUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
