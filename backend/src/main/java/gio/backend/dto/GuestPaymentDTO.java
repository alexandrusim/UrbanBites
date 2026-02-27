package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru plată guest (fără autentificare)")
public class GuestPaymentDTO {

    @NotNull(message = "ID-ul rezervării este obligatoriu")
    @Schema(description = "ID-ul rezervării pentru care se face plata", example = "15", required = true)
    private Integer reservationId;

    @NotNull(message = "Suma este obligatorie")
    @DecimalMin(value = "0.01", message = "Suma trebuie să fie mai mare de 0")
    @Schema(description = "Suma de plată", example = "50.00", required = true)
    private BigDecimal amount;

    @NotBlank(message = "Moneda este obligatorie")
    @Size(max = 3)
    @Schema(description = "Moneda plății", example = "RON", required = true)
    private String currency;

    @NotBlank(message = "Metoda de plată este obligatorie")
    @Schema(description = "Metoda de plată", example = "CARD", required = true)
    private String paymentMethod; // CARD, CASH, ONLINE

    @NotBlank(message = "Furnizorul de plată este obligatoriu")
    @Schema(description = "Furnizorul de plată", example = "STRIPE", required = true)
    private String paymentProvider;

    @Schema(description = "Detalii suplimentare despre plată (opțional)", example = "Payment for table reservation")
    private String paymentDetails;

    // Card details for CARD payments
    @Schema(description = "Numele deținătorului cardului", example = "Ion Popescu")
    private String cardHolderName;

    @Pattern(regexp = "^[0-9]{16}$", message = "Numărul cardului trebuie să aibă 16 cifre")
    @Schema(description = "Numărul cardului (16 cifre)", example = "4111111111111111")
    private String cardNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Data expirării trebuie să fie în formatul MM/YY")
    @Schema(description = "Data expirării cardului (MM/YY)", example = "12/25")
    private String expiryDate;

    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV trebuie să aibă 3 sau 4 cifre")
    @Schema(description = "Codul CVV (3-4 cifre)", example = "123")
    private String cvv;
}
