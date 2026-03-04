package genxsolution.vms.vmsbackend.lookup.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponseDto(
        Instant timestamp,
        int status,
        String error,
        String message,
        List<String> supportedEnumKeys
) {
}






