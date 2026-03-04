package genxsolution.vms.vmsbackend.authentication;

import genxsolution.vms.vmsbackend.role_permission_system.shared.dto.*;
import genxsolution.vms.vmsbackend.lookup.dto.ErrorResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponseDto(
                            Instant.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            ex.getMessage(),
                            List.of()
                    )
            );
        } catch (Exception ex) {
            log.error("Unhandled login error for username={}", request == null ? null : request.username(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ErrorResponseDto(
                            Instant.now(),
                            HttpStatus.BAD_REQUEST.value(),
                            HttpStatus.BAD_REQUEST.getReasonPhrase(),
                            "Login failed",
                            List.of()
                    )
            );
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestBody(required = false) LogoutRequest request, @RequestHeader(value = "X-Session-Token", required = false) String tokenHeader) {
        String token = request != null && request.sessionToken() != null ? request.sessionToken() : tokenHeader;
        if (token != null) {
            authService.logout(token, request == null ? "LOGOUT" : request.reason());
        }
    }

    @PostMapping("/change-password")
    public void changePassword(@RequestHeader("X-Session-Token") String token, @RequestBody ChangePasswordRequest request) {
        authService.changeOwnPassword(authService.authenticate(token), request);
    }

    @PostMapping("/ping")
    public AuthContext ping(@RequestHeader("X-Session-Token") String token) {
        return authService.authenticate(token);
    }

    @PostMapping("/admin/reset-password")
    public void resetPassword(@RequestHeader("X-Session-Token") String token, @RequestBody ResetPasswordRequest request) {
        authService.adminResetPassword(authService.authenticate(token), request);
    }

    @GetMapping("/sessions")
    public List<ActiveSessionResponse> activeSessions(@RequestHeader("X-Session-Token") String token) {
        return authService.listActiveSessions(authService.authenticate(token));
    }

    @DeleteMapping("/sessions/{sessionId}")
    public void revokeSession(@RequestHeader("X-Session-Token") String token, @PathVariable UUID sessionId) {
        authService.revokeSession(authService.authenticate(token), sessionId);
    }
}






