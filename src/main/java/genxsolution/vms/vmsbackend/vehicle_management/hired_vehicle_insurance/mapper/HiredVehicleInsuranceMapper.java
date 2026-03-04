package genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.mapper;

import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.dto.HiredVehicleInsuranceResponse;
import genxsolution.vms.vmsbackend.vehicle_management.hired_vehicle_insurance.model.HiredVehicleInsurance;
import org.springframework.stereotype.Component;

@Component
public class HiredVehicleInsuranceMapper {
    public HiredVehicleInsuranceResponse toResponse(HiredVehicleInsurance m) {
        return new HiredVehicleInsuranceResponse(
                m.insuranceId(),
                m.vehicleId(),
                m.supplierId(),
                m.hiredVehicleId(),
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
                m.HiredVehicleKeyNumber(),
                m.companyHiredVehicleRegistrationNumber(),
                m.HiredVehicleChassisNumber()
        );
    }
}



