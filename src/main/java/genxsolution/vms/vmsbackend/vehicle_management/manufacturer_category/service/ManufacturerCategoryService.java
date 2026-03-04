package genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto.ManufacturerCategoryResponse;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto.ManufacturerCategoryUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.mapper.ManufacturerCategoryMapper;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.repository.ManufacturerCategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ManufacturerCategoryService {
    private final ManufacturerCategoryRepository repository;
    private final ManufacturerCategoryMapper mapper;

    public ManufacturerCategoryService(ManufacturerCategoryRepository repository, ManufacturerCategoryMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ManufacturerCategoryResponse> list() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public ManufacturerCategoryResponse getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("ManufacturerCategory", id.toString()));
    }

    public ManufacturerCategoryResponse create(ManufacturerCategoryUpsertRequest r) {
        return mapper.toResponse(repository.create(r));
    }

    public ManufacturerCategoryResponse update(UUID id, ManufacturerCategoryUpsertRequest r) {
        return repository.update(id, r)
                .map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("ManufacturerCategory", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) {
            throw new VehicleResourceNotFoundException("ManufacturerCategory", id.toString());
        }
    }
}
