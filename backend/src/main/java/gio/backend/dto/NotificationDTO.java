package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru notificare")
public class NotificationDTO {

    @Schema(description = "ID-ul notificării", example = "1")
    private Integer notificationId;

    @NotNull(message = "User ID este obligatoriu")
    @Schema(description = "ID-ul utilizatorului", example = "1", required = true)
    private Integer userId;

    @NotBlank(message = "Tipul notificării este obligatoriu")
    @Schema(description = "Tipul notificării", example = "IN_APP", required = true)
    private String type;

    @Schema(description = "Titlul notificării", example = "Rezervare confirmată")
    private String title;

    @NotBlank(message = "Mesajul este obligatoriu")
    @Schema(description = "Mesajul notificării", example = "Rezervarea dvs. a fost confirmată", required = true)
    private String message;

    @Schema(description = "Tipul entității asociate", example = "RESERVATION")
    private String relatedType;

    @Schema(description = "ID-ul entității asociate", example = "5")
    private Integer relatedId;

    @Schema(description = "ID-ul rezervării asociate (backward compatibility)", example = "5")
    private Integer relatedReservationId;

    @Schema(description = "Statusul notificării", example = "READ")
    private String status;

    @Schema(description = "Notificarea a fost citită", example = "false")
    private Boolean isRead;

    @Schema(description = "Data creării notificării")
    private LocalDateTime createdAt;

    @Schema(description = "Data când notificarea a fost trimisă")
    private LocalDateTime sentAt;

    @Schema(description = "Data când notificarea a fost citită")
    private LocalDateTime readAt;
}
