package genxsolution.vms.vmsbackend.vehicle_management.distributor.dto;

public record DistributorUpsertRequest(
        java.util.UUID manufacturerId,
        String distributorName,
        String distributorCountry,
        String distributorLocation,
        String distributorLogo,
        String distributorPhoneNumber,
        String distributorEmail,
        String distributorDescription,
        Boolean isActive
) {}
