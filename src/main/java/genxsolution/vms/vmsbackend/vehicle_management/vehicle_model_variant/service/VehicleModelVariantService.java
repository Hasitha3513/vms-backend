package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto.VehicleModelVariantResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto.VehicleModelVariantUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.mapper.VehicleModelVariantMapper;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.repository.VehicleModelVariantRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleModelVariantService {
    private final VehicleModelVariantRepository repository;
    private final VehicleModelVariantMapper mapper;

    public VehicleModelVariantService(VehicleModelVariantRepository repository, VehicleModelVariantMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<VehicleModelVariantResponse> list() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public VehicleModelVariantResponse getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleModelVariant", id.toString()));
    }

    public VehicleModelVariantResponse create(VehicleModelVariantUpsertRequest r) {
        return mapper.toResponse(repository.create(r));
    }

    public VehicleModelVariantResponse update(UUID id, VehicleModelVariantUpsertRequest r) {
        return repository.update(id, r)
                .map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleModelVariant", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) {
            throw new VehicleResourceNotFoundException("VehicleModelVariant", id.toString());
        }
    }
}
