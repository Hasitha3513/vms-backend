package genxsolution.vms.vmsbackend.authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AccessWebConfig implements WebMvcConfigurer {

    private final SessionAuthInterceptor sessionAuthInterceptor;
    private final TransactionAuditInterceptor transactionAuditInterceptor;

    public AccessWebConfig(SessionAuthInterceptor sessionAuthInterceptor, TransactionAuditInterceptor transactionAuditInterceptor) {
        this.sessionAuthInterceptor = sessionAuthInterceptor;
        this.transactionAuditInterceptor = transactionAuditInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionAuthInterceptor);
        registry.addInterceptor(transactionAuditInterceptor);
    }
}






