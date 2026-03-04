package genxsolution.vms.vmsbackend.role_permission_system.shared.controller;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AccessLookupResponse;
import genxsolution.vms.vmsbackend.role_permission_system.shared.service.AccessLookupService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/access/dropdowns")
public class AccessLookupController {

    private final AccessLookupService service;

    public AccessLookupController(AccessLookupService service) {
        this.service = service;
    }

    @GetMapping("/core")
    public AccessLookupResponse core(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return service.dropdowns(activeOnly);
    }
}







