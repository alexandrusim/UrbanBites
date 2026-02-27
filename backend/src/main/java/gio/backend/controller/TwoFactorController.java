package gio.backend.controller;

import gio.backend.dto.TwoFaSetupDTO;
import gio.backend.dto.TwoFaVerifyDTO;
import gio.backend.dto.TwoFaLoginVerifyDTO;
import gio.backend.service.TwoFactorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/2fa")
@RequiredArgsConstructor
@Tag(name = "Two-Factor Authentication (2FA)", 
     description = "API pentru Two-Factor Authentication cu Google Authenticator (TOTP). " +
                   "Login flow: 1) POST /api/auth/login → token + twoFaEnabled flag, " +
                   "2) Dacă twoFaEnabled=true, POST /api/2fa/verify-login/{userId} cu codul TOTP")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;

    @Operation(
            summary = "Inițiază setup 2FA - Pasul 1",
            description = "Generează secret TOTP și QR code pentru setup 2FA. " +
                         "Utilizatorul trebuie să scandeze QR code-ul cu Google Authenticator. " +
                         "După scandare, apelează /api/2fa/verify/{userId} cu codul generat de app."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Secret și QR code generate cu succes. " +
                         "Response conține: secret (string), qrCode (base64 PNG), email"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "401", description = "Neautorizat - token invalid")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/setup/{userId}")
    public ResponseEntity<TwoFaSetupDTO> setupTwoFa(
            @Parameter(description = "ID-ul utilizatorului curent", required = true)
            @PathVariable Integer userId) {
        TwoFaSetupDTO response = twoFactorService.setupTwoFa(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Verify 2FA code și enable - Pasul 2",
            description = "Verifică codul TOTP (6 cifre) din Google Authenticator și enable 2FA dacă e corect. " +
                         "Secret-ul trebuie trimis din response-ul pasului 1 (/api/2fa/setup). " +
                         "Codul se schimbă la fiecare 30 de secunde."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA enabled cu succes. Response: { success: true, message: '2FA has been enabled successfully' }"),
            @ApiResponse(responseCode = "400", description = "Cod invalid, expirat, sau lungime greșită (trebuie 6 cifre)"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/verify/{userId}")
    public ResponseEntity<?> verifyAndEnable2Fa(
            @Parameter(description = "ID-ul utilizatorului curent", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody TwoFaVerifyDTO request) {
        
        boolean verified = twoFactorService.verifyAndEnable2Fa(userId, request.getSecret(), request.getCode());
        
        if (verified) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "2FA has been enabled successfully");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid or expired code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(
            summary = "Verify 2FA code la login - Pasul final",
            description = "Endpoint pentru verificarea codului TOTP (6 cifre) în fluxul de login. " +
                         "Flow: 1) POST /api/auth/login cu credențiale, 2) Dacă twoFaEnabled=true, " +
                         "3) User scanează codul din Google Authenticator, 4) POST /api/2fa/verify-login/{userId} cu codul 6 cifre. " +
                         "Dacă success=true, sesiunea e autentificată și cu 2FA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA verification successful. Response: { success: true, message: '2FA verification successful' }. Session e autentificată."),
            @ApiResponse(responseCode = "400", description = "Cod invalid, expirat, sau utilizatorul nu are 2FA enabled"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit")
    })
    @PostMapping("/verify-login/{userId}")
    public ResponseEntity<?> verify2FaLogin(
            @Parameter(description = "ID-ul utilizatorului (din login response)", required = true)
            @PathVariable Integer userId,
            @Valid @RequestBody TwoFaLoginVerifyDTO request) {
        
        boolean verified = twoFactorService.verify2FaCodeForLogin(userId, request.getCode());
        
        if (verified) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "2FA verification successful");
            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid or expired code");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @Operation(
            summary = "Disable 2FA",
            description = "Dezactivează Two-Factor Authentication pentru utilizator. După apel, 2FA nu mai e cerut la login."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA disabled cu succes. Response: { success: true, message: '2FA has been disabled' }"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/disable/{userId}")
    public ResponseEntity<?> disable2Fa(
            @Parameter(description = "ID-ul utilizatorului curent", required = true)
            @PathVariable Integer userId) {
        
        twoFactorService.disable2Fa(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "2FA has been disabled");
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Verify 2FA status",
            description = "Returnează dacă 2FA e enabled pentru utilizator."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2FA status"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/status/{userId}")
    public ResponseEntity<?> get2FaStatus(
            @Parameter(description = "ID-ul utilizatorului", required = true)
            @PathVariable Integer userId) {
        
        boolean status = twoFactorService.get2FaStatus(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("enabled", status);
        response.put("twoFaEnabled", status);
        return ResponseEntity.ok(response);
    }
}
