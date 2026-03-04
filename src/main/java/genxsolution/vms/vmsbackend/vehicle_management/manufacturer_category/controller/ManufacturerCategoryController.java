package genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto.ManufacturerCategoryResponse;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto.ManufacturerCategoryUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.service.ManufacturerCategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/manufacturer-categories")
public class ManufacturerCategoryController {
    private final ManufacturerCategoryService service;

    public ManufacturerCategoryController(ManufacturerCategoryService service) {
        this.service = service;
    }

    @GetMapping
    public java.util.List<ManufacturerCategoryResponse> list(@RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(), filters, Set.of());
    }

    @GetMapping("/{id}")
    public ManufacturerCategoryResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ManufacturerCategoryResponse create(@RequestBody ManufacturerCategoryUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public ManufacturerCategoryResponse update(@PathVariable UUID id, @RequestBody ManufacturerCategoryUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
