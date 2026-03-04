package genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.service;

import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.dto.UserServiceAccessResponse;
import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.dto.UserServiceAccessUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.mapper.UserServiceAccessMapper;
import genxsolution.vms.vmsbackend.role_permission_system.user_service_acces.repository.UserServiceAccessRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceAccessService {
    private final UserServiceAccessRepository repository;
    private final UserServiceAccessMapper mapper;
    public UserServiceAccessService(UserServiceAccessRepository repository, UserServiceAccessMapper mapper){ this.repository=repository; this.mapper=mapper; }
    public List<UserServiceAccessResponse> list(){ return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public UserServiceAccessResponse upsert(UserServiceAccessUpsertRequest request){ genxsolution.vms.vmsbackend.authentication.AccessGuard.requireAdmin(); return mapper.toResponse(repository.upsert(request)); }
    public void delete(UUID userId, UUID serviceId){ genxsolution.vms.vmsbackend.authentication.AccessGuard.requireAdmin(); repository.delete(userId, serviceId); }
}










