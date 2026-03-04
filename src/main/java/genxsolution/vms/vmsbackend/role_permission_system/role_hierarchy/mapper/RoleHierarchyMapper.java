package genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.mapper;

import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.dto.RoleHierarchyResponse;
import genxsolution.vms.vmsbackend.role_permission_system.role_hierarchy.model.RoleHierarchy;
import org.springframework.stereotype.Component;

@Component
public class RoleHierarchyMapper {
    public RoleHierarchyResponse toResponse(RoleHierarchy m) {
        return new RoleHierarchyResponse(m.hierarchy_id(), m.parent_role_id(), m.child_role_id(), m.created_at());
    }
}










