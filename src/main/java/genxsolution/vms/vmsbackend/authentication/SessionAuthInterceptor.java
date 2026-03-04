package genxsolution.vms.vmsbackend.authentication;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public SessionAuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/login") || path.startsWith("/api/v1/enums")) {
            return true;
        }
        if (!path.startsWith("/api/v1/")) {
            return true;
        }

        String token = request.getHeader("X-Session-Token");
        if (token == null || token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        try {
            AuthContext context = authService.authenticate(token);
            if (isAdminRestrictedMethod(request) && !context.superAdmin() && !context.companyAdmin()) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                return false;
            }
            AuthContextHolder.set(context);
            return true;
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContextHolder.clear();
    }

    private boolean isAdminRestrictedMethod(HttpServletRequest request) {
        String method = request.getMethod();
        return "PUT".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method);
    }
}






