package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru verify 2FA code")
public class TwoFaVerifyDTO {
    
    @NotBlank(message = "Secret este obligatoriu")
    @Schema(description = "Secret key din setup", required = true, example = "JBSWY3DPEBLW64TMMQ======")
    private String secret;
    
    @NotBlank(message = "Codul 2FA este obligatoriu")
    @Schema(description = "Codul 6 cifre din Google Authenticator", required = true, example = "123456")
    private String code;
}
