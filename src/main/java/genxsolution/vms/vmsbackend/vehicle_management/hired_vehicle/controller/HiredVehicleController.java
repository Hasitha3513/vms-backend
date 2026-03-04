package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleOverviewResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleModelPrefillResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleOwnershipTypeOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleSupplierOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.dto.HiredVehicleUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle.service.HiredVehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/hired-vehicles")
public class HiredVehicleController {
    private final HiredVehicleService service;

    public HiredVehicleController(HiredVehicleService service) {
        this.service = service;
    }

    @GetMapping({"", "/"})
    public List<HiredVehicleResponse> list(@RequestParam(required = false) UUID supplierId, @RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(supplierId), filters, Set.of("supplierId"));
    }

    @GetMapping("/list")
    public List<HiredVehicleResponse> listAlias(@RequestParam(required = false) UUID supplierId, @RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(supplierId), filters, Set.of("supplierId"));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public HiredVehicleResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/profile/{id:[0-9a-fA-F\\-]{36}}")
    public HiredVehicleResponse getProfileById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/first")
    public HiredVehicleResponse getFirst(@RequestParam(required = false) UUID supplierId) {
        return service.getFirst(supplierId);
    }

    @GetMapping("/overview")
    public HiredVehicleOverviewResponse overview(@RequestParam(required = false) UUID supplierId) {
        return service.overview(supplierId);
    }

    @GetMapping("/registration-options")
    public List<HiredVehicleResponse> registrationOptions(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer limit
    ) {
        return service.registrationOptions(query, limit);
    }

    @GetMapping("/supplier-options")
    public List<HiredVehicleSupplierOptionResponse> supplierOptions() {
        return service.supplierOptions();
    }

    @GetMapping("/ownership-type-options")
    public List<HiredVehicleOwnershipTypeOptionResponse> ownershipTypeOptions() {
        return service.ownershipTypeOptions();
    }

    @GetMapping("/model-prefill")
    public ResponseEntity<HiredVehicleModelPrefillResponse> modelPrefill(
            @RequestParam UUID modelId,
            @RequestParam(required = false) UUID supplierId
    ) {
        return service.modelPrefill(modelId, supplierId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/current-ownership-options")
    public List<HiredVehicleSupplierOptionResponse> currentOwnershipOptions(
            @RequestParam(required = false) UUID supplierId,
            @RequestParam(required = false) Integer ownershipTypeId
    ) {
        return service.currentOwnershipOptions(supplierId, ownershipTypeId);
    }

    @GetMapping("/next-identification")
    public Map<String, String> nextIdentification(
            @RequestParam(required = false) UUID supplierId,
            @RequestParam UUID typeId
    ) {
        return Map.of("value", service.nextIdentificationCode(supplierId, typeId));
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public HiredVehicleResponse create(@RequestBody HiredVehicleUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public HiredVehicleResponse update(@PathVariable UUID id, @RequestBody HiredVehicleUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
