package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru verify 2FA code la login")
public class TwoFaLoginVerifyDTO {
    
    @Schema(description = "Codul 6 cifre din Google Authenticator", required = true, example = "123456")
    private String code;
}
