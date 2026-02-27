package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru meniu")
public class MenuDTO {

    @Schema(description = "ID-ul meniului", example = "1")
    private Integer menuId;

    @NotNull(message = "Restaurant ID este obligatoriu")
    @Schema(description = "ID-ul restaurantului", example = "1", required = true)
    private Integer restaurantId;

    @NotBlank(message = "Numele meniului este obligatoriu")
    @Size(max = 100, message = "Numele meniului nu poate depăși 100 caractere")
    @Schema(description = "Numele meniului", example = "Meniu Italian", required = true)
    private String name;

    @Schema(description = "Descrierea meniului", example = "Preparate tradiționale italiene")
    private String description;

    @Schema(description = "Meniul este activ", example = "true")
    private Boolean isActive;

    @Schema(description = "Ora de start pentru disponibilitate", example = "12:00:00")
    private LocalTime availableFrom;

    @Schema(description = "Ora de final pentru disponibilitate", example = "22:00:00")
    private LocalTime availableTo;

    @Schema(description = "Zilele în care meniul este disponibil", example = "Luni,Marti,Miercuri")
    private String validDays;

    @Schema(description = "Data creării")
    private LocalDateTime createdAt;

    @Schema(description = "Data ultimei actualizări")
    private LocalDateTime updatedAt;
}
