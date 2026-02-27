package gio.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PopularMenuItemDTO {
    private Integer menuItemId;
    private String name;
    private String category;
    private Long orderCount;
    private Double averageRating;
}
