package genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.dto.VehicleTypeResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.dto.VehicleTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_type.service.VehicleTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/vehicles/types")
public class VehicleTypeController {
    private static final Set<String> ALLOWED_FILTERS = Set.of(
            "categoryId",
            "typeName",
            "typeName_like",
            "undercarriageTypeId",
            "fuelTypeId",
            "isActive",
            "usageType",
            "q",
            "sortBy",
            "sortDir"
    );
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("typeName", "fuelTypeId", "undercarriageTypeId", "isActive", "usageType", "categoryId");
    private final VehicleTypeService service;

    public VehicleTypeController(VehicleTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<VehicleTypeResponse> list(@RequestParam Map<String, String> filters) {
        Map<String, String> effective = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : filters.entrySet()) {
            if (ALLOWED_FILTERS.contains(e.getKey())) {
                effective.put(e.getKey(), e.getValue());
            }
        }
        // Keep search scoped to type name only.
        String q = effective.get("q");
        if (q != null && !q.isBlank() && !effective.containsKey("typeName_like")) {
            effective.put("typeName_like", q);
            effective.remove("q");
        }
        String sortBy = effective.get("sortBy");
        if (sortBy != null && !ALLOWED_SORT_FIELDS.contains(sortBy)) {
            effective.remove("sortBy");
            effective.remove("sortDir");
        }
        return ListQueryEngine.apply(service.list(), effective, Set.of());
    }

    @GetMapping("/{id}")
    public VehicleTypeResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VehicleTypeResponse create(@RequestBody VehicleTypeUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public VehicleTypeResponse update(@PathVariable UUID id, @RequestBody VehicleTypeUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
