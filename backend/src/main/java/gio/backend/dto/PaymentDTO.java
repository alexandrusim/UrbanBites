package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru plată")
public class PaymentDTO {

    @Schema(description = "ID-ul plății", example = "1")
    private Integer paymentId;

    @NotNull(message = "Reservation ID este obligatoriu")
    @Schema(description = "ID-ul rezervării", example = "1", required = true)
    private Integer reservationId;

    @NotNull(message = "User ID este obligatoriu")
    @Schema(description = "ID-ul utilizatorului", example = "1", required = true)
    private Integer userId;

    @NotNull(message = "Suma este obligatorie")
    @DecimalMin(value = "0.01", message = "Suma trebuie să fie mai mare decât 0")
    @Schema(description = "Suma plătită", example = "150.50", required = true)
    private BigDecimal amount;

    @Schema(description = "Moneda", example = "RON")
    private String currency;

    @NotBlank(message = "Metoda de plată este obligatorie")
    @Schema(description = "Metoda de plată", example = "CARD", required = true)
    private String paymentMethod;

    @Schema(description = "Statusul plății", example = "COMPLETED")
    private String paymentStatus;

    @Schema(description = "ID-ul tranzacției externe", example = "TXN123456789")
    private String transactionId;

    @Schema(description = "Furnizorul de plăți", example = "Stripe")
    private String paymentProvider;

    @Schema(description = "Detalii sau note despre plată", example = "Plată procesată cu succes")
    private String paymentDetails;

    @Schema(description = "Data creării plății")
    private LocalDateTime createdAt;

    @Schema(description = "Data când plata a fost efectuată")
    private LocalDateTime paidAt;

    @Schema(description = "Data procesării plății")
    private LocalDateTime processedAt;

    @Schema(description = "Data când plata a fost returnată")
    private LocalDateTime refundedAt;

    @Schema(description = "Suma returnată")
    private BigDecimal refundAmount;
}
