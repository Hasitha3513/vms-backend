package genxsolution.vms.vmsbackend.vehicle_management.distributor.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.dto.DistributorResponse;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.dto.DistributorUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.service.DistributorService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/distributors")
public class DistributorController {
    private final DistributorService service;

    public DistributorController(DistributorService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<DistributorResponse> list(@RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(), filters, Set.of());
    }

    @GetMapping("/{id}")
    public DistributorResponse getById(@PathVariable UUID id) { return service.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DistributorResponse create(@RequestBody DistributorUpsertRequest r) { return service.create(r); }

    @PutMapping("/{id}")
    public DistributorResponse update(@PathVariable UUID id, @RequestBody DistributorUpsertRequest r) { return service.update(id, r); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) { service.delete(id); }
}
