package gio.backend.dto;

import gio.backend.enums.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO complet pentru rezervare cu detalii guest")
public class ReservationDetailDTO {

    @Schema(description = "ID-ul rezervării", example = "1")
    private Integer reservationId;

    @Schema(description = "ID-ul utilizatorului (null pentru guest)", example = "1")
    private Integer userId;

    @Schema(description = "Numele complet utilizator/guest", example = "Ion Popescu")
    private String fullName;

    @Schema(description = "Email utilizator/guest", example = "ion.popescu@email.com")
    private String email;

    @Schema(description = "Telefon utilizator/guest", example = "+40712345678")
    private String phoneNumber;

    @Schema(description = "Este rezervare guest (fără cont)", example = "true")
    private Boolean isGuest;

    @Schema(description = "ID-ul restaurantului", example = "1")
    private Integer restaurantId;

    @Schema(description = "Numele restaurantului", example = "Restaurant Italian")
    private String restaurantName;

    @Schema(description = "ID-ul mesei", example = "5")
    private Integer tableId;

    @Schema(description = "Numărul mesei", example = "A5")
    private String tableNumber;

    @Schema(description = "Data rezervării", example = "2026-01-20")
    private LocalDate reservationDate;

    @Schema(description = "Ora rezervării", example = "19:00:00")
    private LocalTime reservationTime;

    @Schema(description = "Durata în minute", example = "120")
    private Integer durationMinutes;

    @Schema(description = "Numărul de persoane", example = "4")
    private Integer numberOfGuests;

    @Schema(description = "Statusul rezervării", example = "PENDING")
    private ReservationStatus status;

    @Schema(description = "Cerințe speciale", example = "Alergie la nuci")
    private String specialRequests;

    @Schema(description = "Cod de confirmare", example = "ABC123XYZ")
    private String confirmationCode;

    @Schema(description = "Data creării", example = "2026-01-10T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data actualizării", example = "2026-01-10T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Data check-in", example = "2026-01-20T19:05:00")
    private LocalDateTime checkedInAt;

    @Schema(description = "Data anulării", example = "2026-01-15T14:20:00")
    private LocalDateTime cancelledAt;

    @Schema(description = "Motiv anulare", example = "Schimbare de planuri")
    private String cancellationReason;
}
