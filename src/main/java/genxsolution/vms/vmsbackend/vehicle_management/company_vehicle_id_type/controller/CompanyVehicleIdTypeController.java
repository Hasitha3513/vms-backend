package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto.CompanyVehicleIdTypeResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.dto.CompanyVehicleIdTypeUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle_id_type.service.CompanyVehicleIdTypeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/company-vehicle-id-types")
public class CompanyVehicleIdTypeController {
    private final CompanyVehicleIdTypeService service;

    public CompanyVehicleIdTypeController(CompanyVehicleIdTypeService service) {
        this.service = service;
    }

    @GetMapping
    public List<CompanyVehicleIdTypeResponse> list(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId"));
    }

    @GetMapping("/{id}")
    public CompanyVehicleIdTypeResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyVehicleIdTypeResponse create(@RequestBody CompanyVehicleIdTypeUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public CompanyVehicleIdTypeResponse update(@PathVariable UUID id, @RequestBody CompanyVehicleIdTypeUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
