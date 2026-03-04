package genxsolution.vms.vmsbackend.vehicle_management.distributor.model;

import java.time.Instant;
import java.util.UUID;

public record Distributor(
        UUID distributorId,
        UUID manufacturerId,
        String distributorName,
        String distributorCountry,
        String distributorLocation,
        String distributorLogo,
        String distributorPhoneNumber,
        String distributorEmail,
        String distributorDescription,
        Boolean isActive,
        Instant createAt,
        Instant updateAt
) {}
