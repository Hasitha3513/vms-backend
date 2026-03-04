package genxsolution.vms.vmsbackend.role_permission_system.system_module.service;

import genxsolution.vms.vmsbackend.role_permission_system.system_module.dto.SystemModuleResponse;
import genxsolution.vms.vmsbackend.role_permission_system.system_module.dto.SystemModuleUpsertRequest;
import genxsolution.vms.vmsbackend.role_permission_system.shared.exception.AccessResourceNotFoundException;
import genxsolution.vms.vmsbackend.role_permission_system.system_module.mapper.SystemModuleMapper;
import genxsolution.vms.vmsbackend.role_permission_system.system_module.repository.SystemModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SystemModuleService {
    private final SystemModuleRepository repository;
    private final SystemModuleMapper mapper;
    public SystemModuleService(SystemModuleRepository repository, SystemModuleMapper mapper) { this.repository = repository; this.mapper = mapper; }
    public List<SystemModuleResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public SystemModuleResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("SystemModule", id.toString())); }
    public SystemModuleResponse create(SystemModuleUpsertRequest request) { return mapper.toResponse(repository.create(request)); }
    public SystemModuleResponse update(UUID id, SystemModuleUpsertRequest request) { return repository.update(id, request).map(mapper::toResponse).orElseThrow(() -> new AccessResourceNotFoundException("SystemModule", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) { throw new AccessResourceNotFoundException("SystemModule", id.toString()); } }
}









