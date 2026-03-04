package genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.controller;

import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.dto.UserServiceAccessResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.dto.UserServiceAccessUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.service.UserServiceAccessService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/access/user-service-access")
public class UserServiceAccessController {
    private final UserServiceAccessService service;
    public UserServiceAccessController(UserServiceAccessService service){ this.service=service; }
    @GetMapping public List<UserServiceAccessResponse> list(){ return service.list(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public UserServiceAccessResponse upsert(@RequestBody UserServiceAccessUpsertRequest request){ return service.upsert(request); }
    @DeleteMapping public void delete(@RequestParam UUID userId, @RequestParam UUID serviceId){ service.delete(userId, serviceId); }
}









