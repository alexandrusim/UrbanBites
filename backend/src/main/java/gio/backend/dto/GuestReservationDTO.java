package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru creare rezervare de către guest (utilizator neautentificat)")
public class GuestReservationDTO {

    @NotBlank(message = "Prenumele este obligatoriu")
    @Size(max = 100, message = "Prenumele nu poate depăși 100 caractere")
    @Schema(description = "Prenumele clientului", example = "Ion", required = true)
    private String firstName;

    @NotBlank(message = "Numele este obligatoriu")
    @Size(max = 100, message = "Numele nu poate depăși 100 caractere")
    @Schema(description = "Numele clientului", example = "Popescu", required = true)
    private String lastName;

    @NotBlank(message = "Email-ul este obligatoriu")
    @Email(message = "Email-ul trebuie să fie valid")
    @Schema(description = "Email-ul clientului", example = "ion.popescu@email.com", required = true)
    private String email;

    @NotBlank(message = "Numărul de telefon este obligatoriu")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Numărul de telefon trebuie să fie valid")
    @Schema(description = "Numărul de telefon", example = "+40712345678", required = true)
    private String phoneNumber;

    @NotNull(message = "Restaurant ID este obligatoriu")
    @Schema(description = "ID-ul restaurantului", example = "1", required = true)
    private Integer restaurantId;

    @Schema(description = "ID-ul mesei preferate (opțional)", example = "5")
    private Integer tableId;

    @NotNull(message = "Data rezervării este obligatorie")
    @Future(message = "Data rezervării trebuie să fie în viitor")
    @Schema(description = "Data rezervării", example = "2026-01-20", required = true)
    private LocalDate reservationDate;

    @NotNull(message = "Ora rezervării este obligatorie")
    @Schema(description = "Ora rezervării", example = "19:00:00", required = true)
    private LocalTime reservationTime;

    @NotNull(message = "Numărul de persoane este obligatoriu")
    @Min(value = 1, message = "Trebuie să fie cel puțin o persoană")
    @Max(value = 50, message = "Numărul maxim de persoane este 50")
    @Schema(description = "Numărul de persoane", example = "4", required = true)
    private Integer numberOfGuests;

    @Schema(description = "Cerințe speciale", example = "Alergie la nuci, masă lângă fereastră")
    private String specialRequests;

    @Schema(description = "Durata estimată în minute", example = "120")
    private Integer durationMinutes;
}
