package gio.backend.controller;

import gio.backend.dto.AuthResponse;
import gio.backend.dto.LoginRequest;
import gio.backend.dto.RegisterRequest;
import gio.backend.entity.User;
import gio.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autentificare", description = "API-uri pentru autentificare și gestionarea utilizatorilor")
public class AuthController {

    private final AuthService authService;

    @Operation(
        summary = "Înregistrare utilizator nou",
        description = "Creează un cont nou de utilizator cu rolul CLIENT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Înregistrare reușită",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "409", description = "Email-ul este deja folosit"),
        @ApiResponse(responseCode = "500", description = "Eroare la înregistrare")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("already in use")) {
                Map<String, String> error = new HashMap<>();
                error.put("message", "Email already in use");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            Map<String, String> error = new HashMap<>();
            error.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(
        summary = "Autentificare",
        description = "Autentifică un utilizator și returnează un token JWT. " +
                     "Dacă utilizatorul are 2FA enabled, response-ul va conține twoFaEnabled=true. " +
                     "În acest caz, client trebuie să apeleze POST /api/2fa/verify-login/{userId} cu codul TOTP."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autentificare reușită. " +
                     "Verificați twoFaEnabled în response. Dacă true, apelați /api/2fa/verify-login/{userId}",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credențiale invalide - email sau password incorect"),
        @ApiResponse(responseCode = "500", description = "Eroare server la autentificare")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(
        summary = "Obține utilizatorul curent",
        description = "Returnează informațiile despre utilizatorul autentificat, incluzând 2FA status",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Utilizator găsit cu 2FA status (twoFaEnabled, twoFaVerified)"),
        @ApiResponse(responseCode = "401", description = "Neautentificat - token lipsă sau invalid"),
        @ApiResponse(responseCode = "500", description = "Eroare server")
    })
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String email = authentication.getName();
            User user = authService.getCurrentUser(email);

            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.getUserId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("role", user.getRole());
            response.put("phoneNumber", user.getPhoneNumber());
            response.put("restaurantId", user.getRestaurantId());
            response.put("twoFaEnabled", user.getTwoFaEnabled());
            response.put("twoFaVerified", user.getTwoFaVerified());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
