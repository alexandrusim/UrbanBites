package gio.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO pentru produs din meniu")
public class MenuItemDTO {

    @Schema(description = "ID-ul produsului", example = "1")
    private Integer itemId;

    @NotNull(message = "Menu ID este obligatoriu")
    @Schema(description = "ID-ul meniului", example = "1", required = true)
    private Integer menuId;

    @NotBlank(message = "Numele produsului este obligatoriu")
    @Size(max = 200, message = "Numele produsului nu poate depăși 200 caractere")
    @Schema(description = "Numele produsului", example = "Pizza Margherita", required = true)
    private String name;

    @Schema(description = "Descrierea produsului", example = "Pizza cu sos de roșii, mozzarella și busuioc")
    private String description;

    @NotNull(message = "Prețul este obligatoriu")
    @DecimalMin(value = "0.01", message = "Prețul trebuie să fie mai mare decât 0")
    @Schema(description = "Prețul produsului", example = "35.99", required = true)
    private BigDecimal price;

    @Schema(description = "Categoria produsului", example = "Pizza")
    private String category;

    @Schema(description = "URL-ul imaginii produsului", example = "https://example.com/images/pizza.jpg")
    private String imageUrl;

    @Schema(description = "Produsul este disponibil", example = "true")
    private Boolean isAvailable;

    @Schema(description = "Produsul conține alergeni", example = "Gluten, Lactate")
    private String allergens;

    @Schema(description = "Data creării")
    private LocalDateTime createdAt;

    @Schema(description = "Data ultimei actualizări")
    private LocalDateTime updatedAt;
}
