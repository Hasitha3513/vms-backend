package genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.dto.ManufacturerCategoryResponse;
import genxsolution.vms.vmsbackend.vehicle_management.manufacturer_category.model.ManufacturerCategory;
import org.springframework.stereotype.Component;

@Component
public class ManufacturerCategoryMapper {
    public ManufacturerCategoryResponse toResponse(ManufacturerCategory m) {
        return new ManufacturerCategoryResponse(m.id(), m.manufacturerId(), m.categoryId());
    }
}
