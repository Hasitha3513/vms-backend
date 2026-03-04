package genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.dto.VehicleInsuranceResponse;
import genxsolution.vms.vmsbackend.vehicle_management.vehicle_insurance.model.VehicleInsurance;
import org.springframework.stereotype.Component;

@Component
public class VehicleInsuranceMapper {
    public VehicleInsuranceResponse toResponse(VehicleInsurance m) {
        return new VehicleInsuranceResponse(
                m.insuranceId(),
                m.vehicleId(),
                m.companyId(),
                m.companyVehicleId(),
                m.insuranceCompany(),
                m.policyNumber(),
                m.insuranceTypeId(),
                m.policyStartDate(),
                m.policyExpiryDate(),
                m.idvAmount(),
                m.premiumAmount(),
                m.paymentMode(),
                m.paymentDate(),
                m.agentName(),
                m.agentContact(),
                m.agentEmail(),
                m.nomineeName(),
                m.addOnCovers(),
                m.ncbPercent(),
                m.claimCount(),
                m.lastClaimDate(),
                m.lastClaimAmount(),
                m.renewalReminderDays(),
                m.insuranceStatus(),
                m.notes(),
                m.isCurrent(),
                m.createdAt(),
                m.updatedAt(),
                m.companyName(),
                m.companyVehicleKeyNumber(),
                m.companyVehicleRegistrationNumber(),
                m.companyVehicleChassisNumber()
        );
    }
}
