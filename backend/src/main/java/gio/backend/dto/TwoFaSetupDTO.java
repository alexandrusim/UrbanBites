package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru setup 2FA")
public class TwoFaSetupDTO {
    
    @Schema(description = "Secret key pentru TOTP", example = "JBSWY3DPEBLW64TMMQ======")
    private String secret;
    
    @Schema(description = "QR Code ca Base64 PNG image", example = "data:image/png;base64,...")
    private String qrCode;
    
    @Schema(description = "Email-ul utilizatorului", example = "user@example.com")
    private String email;
}
