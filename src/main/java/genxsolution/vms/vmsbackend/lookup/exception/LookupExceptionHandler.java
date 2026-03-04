package genxsolution.vms.vmsbackend.lookup.exception;

import genxsolution.vms.vmsbackend.lookup.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class LookupExceptionHandler {

    @ExceptionHandler(EnumTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleEnumTypeNotSupported(EnumTypeNotSupportedException ex) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                ex.getSupportedEnumKeys()
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(LookupResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleLookupResourceNotFound(LookupResourceNotFoundException ex) {
        ErrorResponseDto response = new ErrorResponseDto(
                Instant.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}






