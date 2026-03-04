package genxsolution.vms.vmsbackend.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TransactionAuditInterceptor implements HandlerInterceptor {

    private final TransactionAuditService auditService;

    public TransactionAuditInterceptor(TransactionAuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String action = request.getMethod();
        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        auditService.log(action, path, response.getStatus(), ip, userAgent);
    }
}






