package gio.backend.dto;

import gio.backend.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatusUpdateDTO {
    @NotNull
    private ReservationStatus status;
    
    private String reason;
}
