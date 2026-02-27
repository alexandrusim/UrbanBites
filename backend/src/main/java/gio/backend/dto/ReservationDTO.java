package gio.backend.dto;

import gio.backend.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Integer reservationId;
    private Integer userId;
    private String userName;
    private String userEmail;
    private Integer restaurantId;
    private String restaurantName;
    private Integer tableId;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private Integer durationMinutes;
    private Integer numberOfGuests;
    private ReservationStatus status;
    private String specialRequests;
    private String confirmationCode;
    private LocalDateTime createdAt;
}
