package gio.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long totalUsers;
    private long totalRestaurants;
    private long totalReservations;
    private long pendingReservations;
    private long confirmedReservations;
    private long todayReservations;
}
