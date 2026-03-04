package genxsolution.vms.vmsbackend.role_permission_system.system_service.dto;

public record SystemServiceUpsertRequest(
        Object module_id,
        Object service_code,
        Object service_name,
        Object service_description,
        Object service_path,
        Object icon_name,
        Object parent_service_id,
        Object display_order,
        Object is_active
) {
}










