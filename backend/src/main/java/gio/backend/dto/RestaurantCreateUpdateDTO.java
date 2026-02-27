package gio.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateUpdateDTO {
    @NotBlank
    @Size(max = 200)
    private String name;
    
    private String description;
    
    @NotBlank
    @Size(max = 255)
    private String address;
    
    @NotBlank
    @Size(max = 100)
    private String city;
    
    @Size(max = 20)
    private String postalCode;
    
    @Size(max = 100)
    private String country;
    
    @NotBlank
    @Size(max = 20)
    private String phoneNumber;
    
    @Size(max = 255)
    private String email;
    
    @Size(max = 255)
    private String website;
    
    @Size(max = 500)
    private String logoUrl;
    
    @Size(max = 500)
    private String coverImageUrl;
    
    private String svgLayout;
    
    @Size(max = 100)
    private String cuisineType;
    
    private Integer priceRange;
    
    private Integer capacity;
    
    private Map<String, Object> openingHours;
    
    private Boolean isActive;
}
