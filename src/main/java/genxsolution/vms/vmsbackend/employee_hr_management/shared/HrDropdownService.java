package genxsolution.vms.vmsbackend.employee_hr_management.service;

import genxsolution.vms.vmsbackend.employee_hr_management.dto.dropdown.HrLookupResponse;
import genxsolution.vms.vmsbackend.lookup.service.LookupEnumService;
import org.springframework.stereotype.Service;

@Service
public class HrDropdownService {

    private final LookupEnumService lookupEnumService;

    public HrDropdownService(LookupEnumService lookupEnumService) {
        this.lookupEnumService = lookupEnumService;
    }

    public HrLookupResponse lookups(boolean activeOnly) {
        return new HrLookupResponse(
                lookupEnumService.getEnumValues("employee_category", activeOnly),
                lookupEnumService.getEnumValues("gender", activeOnly),
                lookupEnumService.getEnumValues("employment_type", activeOnly),
                lookupEnumService.getEnumValues("employment_status", activeOnly),
                lookupEnumService.getEnumValues("skill_category", activeOnly),
                lookupEnumService.getEnumValues("skill_level", activeOnly),
                lookupEnumService.getEnumValues("education_level", activeOnly),
                lookupEnumService.getEnumValues("document_type_enum", activeOnly),
                lookupEnumService.getEnumValues("project_member_role", activeOnly),
                lookupEnumService.getEnumValues("training_type", activeOnly),
                lookupEnumService.getEnumValues("training_status", activeOnly),
                lookupEnumService.getEnumValues("complaint_type", activeOnly),
                lookupEnumService.getEnumValues("complaint_priority", activeOnly),
                lookupEnumService.getEnumValues("complaint_status", activeOnly),
                lookupEnumService.getEnumValues("performance_rating", activeOnly),
                lookupEnumService.getEnumValues("attendance_status", activeOnly),
                lookupEnumService.getEnumValues("overtime_type", activeOnly),
                lookupEnumService.getEnumValues("leave_application_status", activeOnly)
        );
    }
}






