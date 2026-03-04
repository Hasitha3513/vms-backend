package genxsolution.vms.vmsbackend.common.cache;

import genxsolution.vms.vmsbackend.authentication.AuthContext;
import genxsolution.vms.vmsbackend.authentication.AuthContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TenantKeyResolver {

    public String tenant() {
        AuthContext context = AuthContextHolder.get();
        if (context == null || context.companyCode() == null || context.companyCode().isBlank()) {
            return "global";
        }
        return context.companyCode().trim().toLowerCase();
    }
}

