package genxsolution.vms.vmsbackend.employee_hr_management.controller;

import genxsolution.vms.vmsbackend.employee_hr_management.dto.dropdown.HrLookupResponse;
import genxsolution.vms.vmsbackend.employee_hr_management.service.HrDropdownService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hr/dropdowns")
public class HrDropdownController {

    private final HrDropdownService dropdownService;

    public HrDropdownController(HrDropdownService dropdownService) {
        this.dropdownService = dropdownService;
    }

    @GetMapping("/core")
    public HrLookupResponse lookups(@RequestParam(defaultValue = "true") boolean activeOnly) {
        return dropdownService.lookups(activeOnly);
    }
}







