package genxsolution.vms.vmsbackend.vehicle_management.distributor.service;

import genxsolution.vms.vmsbackend.vehicle_management.distributor.dto.DistributorResponse;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.dto.DistributorUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.mapper.DistributorMapper;
import genxsolution.vms.vmsbackend.vehicle_management.distributor.repository.DistributorRepository;
import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DistributorService {
    private final DistributorRepository repository;
    private final DistributorMapper mapper;

    public DistributorService(DistributorRepository repository, DistributorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<DistributorResponse> list() { return repository.findAll().stream().map(mapper::toResponse).toList(); }
    public DistributorResponse getById(UUID id) { return repository.findById(id).map(mapper::toResponse).orElseThrow(() -> new VehicleResourceNotFoundException("Distributor", id.toString())); }
    public DistributorResponse create(DistributorUpsertRequest r) { return mapper.toResponse(repository.create(r)); }
    public DistributorResponse update(UUID id, DistributorUpsertRequest r) { return repository.update(id, r).map(mapper::toResponse).orElseThrow(() -> new VehicleResourceNotFoundException("Distributor", id.toString())); }
    public void delete(UUID id) { if (!repository.delete(id)) throw new VehicleResourceNotFoundException("Distributor", id.toString()); }
}
