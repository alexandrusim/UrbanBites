package gio.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
    private Integer userId;
    private String firstName;
    private String lastName;
    private Boolean twoFaEnabled;
    private Boolean twoFaVerified;
}
