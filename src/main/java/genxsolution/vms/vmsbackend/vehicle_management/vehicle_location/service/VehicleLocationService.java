package genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.service;

import genxsolution.vms.vmsbackend.vehicle_management.exception.VehicleResourceNotFoundException;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.dto.VehicleLocationResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.dto.VehicleLocationUpsertRequest;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.mapper.VehicleLocationMapper;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_location.repository.VehicleLocationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class VehicleLocationService {
    private final VehicleLocationRepository repository;
    private final VehicleLocationMapper mapper;

    public VehicleLocationService(VehicleLocationRepository repository, VehicleLocationMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<VehicleLocationResponse> list(UUID companyId, UUID vehicleId) {
        return repository.findAll(companyId, vehicleId).stream().map(mapper::toResponse).toList();
    }

    public VehicleLocationResponse getById(UUID id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleLocation", id.toString()));
    }

    public VehicleLocationResponse create(VehicleLocationUpsertRequest r) {
        validatePeriod(r);
        return mapper.toResponse(repository.create(r));
    }

    public VehicleLocationResponse update(UUID id, VehicleLocationUpsertRequest r) {
        validatePeriod(r);
        return repository.update(id, r)
                .map(mapper::toResponse)
                .orElseThrow(() -> new VehicleResourceNotFoundException("VehicleLocation", id.toString()));
    }

    public void delete(UUID id) {
        if (!repository.delete(id)) throw new VehicleResourceNotFoundException("VehicleLocation", id.toString());
    }

    private void validatePeriod(VehicleLocationUpsertRequest r) {
        if (r.periodStartDate() != null && r.periodEndDate() != null && r.periodEndDate().isBefore(r.periodStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Period End Date cannot be before Period Start Date");
        }
    }
}
