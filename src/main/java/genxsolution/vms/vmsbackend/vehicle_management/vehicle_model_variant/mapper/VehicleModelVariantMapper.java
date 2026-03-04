package genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.dto.VehicleModelVariantResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_model_variant.model.VehicleModelVariant;
import org.springframework.stereotype.Component;

@Component
public class VehicleModelVariantMapper {
    public VehicleModelVariantResponse toResponse(VehicleModelVariant m) {
        return new VehicleModelVariantResponse(
                m.variantId(),
                m.modelId(),
                m.variantName(),
                m.variantCode(),
                m.additionalFeatures(),
                m.priceExShowroom(),
                m.isActive(),
                m.createdAt()
        );
    }
}
