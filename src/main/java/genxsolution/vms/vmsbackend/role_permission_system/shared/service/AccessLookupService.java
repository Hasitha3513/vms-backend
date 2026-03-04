package genxsolution.vms.vmsbackend.role_permission_system.shared.service;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.AccessLookupResponse;
import genxsolution.vms.vmsbackend.lookup.service.LookupEnumService;
import org.springframework.stereotype.Service;

@Service
public class AccessLookupService {

    private final LookupEnumService lookupEnumService;

    public AccessLookupService(LookupEnumService lookupEnumService) {
        this.lookupEnumService = lookupEnumService;
    }

    public AccessLookupResponse dropdowns(boolean activeOnly) {
        return new AccessLookupResponse(
                lookupEnumService.getEnumValues("login_status", activeOnly),
                lookupEnumService.getEnumValues("data_scope_type", activeOnly)
        );
    }
}







