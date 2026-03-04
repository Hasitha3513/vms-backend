package genxsolution.vms.vmsbackend.role_permission_system.system_service.service;

import genxsolution.vms.vmsbackend.role_permission_system.system_service.dto.SystemServiceResponse;
import genxsolution.vms.vmsbackend.role_permission_system.system_service.dto.SystemServiceUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.system_service.mapper.SystemServiceMapper;
import genxsolution.vms.vmsbackend.role_permission_system.system_service.repository.SystemServiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SystemServiceService {
    private final SystemServiceRepository repository;
    private final SystemServiceMapper mapper;
    public SystemServiceService(SystemServiceRepository repository, SystemServiceMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<SystemServiceResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public SystemServiceResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("SystemService", id.toString())); }
    public SystemServiceResponse create(SystemServiceUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public SystemServiceResponse update(UUID id, SystemServiceUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("SystemService", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("SystemService", id.toString()); } }
}









