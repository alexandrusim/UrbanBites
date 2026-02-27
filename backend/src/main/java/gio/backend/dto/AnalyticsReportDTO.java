package gio.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer restaurantId;
    private Long totalReservations;
    private Long confirmedReservations;
    private Long cancelledReservations;
    private BigDecimal totalRevenue;
    private Double averageRating;
    private Integer totalFeedbacks;
}
