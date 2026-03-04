package genxsolution.vms.vmsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VmsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(VmsBackendApplication.class, args);
    }

}






