package genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.controller;

import genxsolution.vms.vmsbackend.common.query.ListQueryEngine;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleOverviewResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleCurrentOwnershipOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.dto.CompanyVehicleOwnershipTypeOptionResponse;
import genxsolution.vms.vmsbackend.vehicle_management.company_vehicle.service.CompanyVehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/company-vehicles")
public class CompanyVehicleController {
    private final CompanyVehicleService service;

    public CompanyVehicleController(CompanyVehicleService service) {
        this.service = service;
    }

    @GetMapping({"", "/"})
    public List<CompanyVehicleResponse> list(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId"));
    }

    @GetMapping("/list")
    public List<CompanyVehicleResponse> listAlias(@RequestParam(required = false) UUID companyId, @RequestParam Map<String, String> filters) {
        return ListQueryEngine.apply(service.list(companyId), filters, Set.of("companyId"));
    }

    @GetMapping("/{id:[0-9a-fA-F\\-]{36}}")
    public CompanyVehicleResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping("/first")
    public CompanyVehicleResponse getFirst(@RequestParam(required = false) UUID companyId) {
        return service.getFirst(companyId);
    }

    @GetMapping("/overview")
    public CompanyVehicleOverviewResponse overview(@RequestParam(required = false) UUID companyId) {
        return service.overview(companyId);
    }

    @GetMapping("/next-identification")
    public Map<String, String> nextIdentification(@RequestParam UUID companyId, @RequestParam UUID typeId) {
        return Map.of("value", service.nextIdentificationCode(companyId, typeId));
    }

    @GetMapping("/ownership-type-options")
    public List<CompanyVehicleOwnershipTypeOptionResponse> ownershipTypeOptions() {
        return service.ownershipTypeOptions();
    }

    @GetMapping("/current-ownership-options")
    public List<CompanyVehicleCurrentOwnershipOptionResponse> currentOwnershipOptions(
            @RequestParam(required = false) UUID companyId,
            @RequestParam(required = false) Integer ownershipTypeId
    ) {
        return service.currentOwnershipOptions(companyId, ownershipTypeId);
    }

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyVehicleResponse create(@RequestBody CompanyVehicleUpsertRequest r) {
        return service.create(r);
    }

    @PutMapping("/{id}")
    public CompanyVehicleResponse update(@PathVariable UUID id, @RequestBody CompanyVehicleUpsertRequest r) {
        return service.update(id, r);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
