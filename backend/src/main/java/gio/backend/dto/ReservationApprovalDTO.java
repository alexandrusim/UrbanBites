package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru actualizare status rezervare de către admin")
public class ReservationApprovalDTO {

    @NotNull(message = "ID-ul rezervării este obligatoriu")
    @Schema(description = "ID-ul rezervării", example = "1", required = true)
    private Integer reservationId;

    @NotBlank(message = "Statusul este obligatoriu")
    @Schema(description = "Noul status", example = "CONFIRMED", required = true, 
            allowableValues = {"CONFIRMED", "CANCELLED", "COMPLETED", "NO_SHOW"})
    private String status;

    @Schema(description = "ID-ul mesei asignate (pentru aprobare)", example = "5")
    private Integer tableId;

    @Schema(description = "Notă/comentariu admin", example = "Rezervare confirmată, masă lângă fereastră")
    private String adminNote;

    @Schema(description = "Motiv refuz (pentru status CANCELLED)", example = "Restaurant închis în acea zi")
    private String rejectionReason;
}
