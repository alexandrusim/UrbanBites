package gio.backend.service;

import gio.backend.dto.DashboardStatsDTO;
import gio.backend.entity.User;
import gio.backend.enums.ReservationStatus;
import gio.backend.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final RestaurantService restaurantService;
    private final ReservationService reservationService;
    private final SecurityService securityService;

    public DashboardStatsDTO getDashboardStats() {
        User currentUser = securityService.getCurrentUser();
        
        DashboardStatsDTO stats = new DashboardStatsDTO();
        
        if (currentUser.getRole() == UserRole.SYSTEM_ADMIN) {
            stats.setTotalUsers(userService.countAllUsers());
            stats.setTotalRestaurants(restaurantService.countAllRestaurants());
            stats.setTotalReservations(reservationService.countAllReservations());
            stats.setPendingReservations(reservationService.countByStatus(ReservationStatus.PENDING));
            stats.setConfirmedReservations(reservationService.countByStatus(ReservationStatus.CONFIRMED));
            stats.setTodayReservations(reservationService.countByDate(LocalDate.now()));
        } else if (currentUser.getRole() == UserRole.ADMIN_RESTAURANT && currentUser.getRestaurantId() != null) {
            Integer restaurantId = currentUser.getRestaurantId();
            stats.setTotalUsers(0L); 
            stats.setTotalRestaurants(1L);
            stats.setTotalReservations(reservationService.countByRestaurantId(restaurantId));
            stats.setPendingReservations(reservationService.countByRestaurantIdAndStatus(restaurantId, ReservationStatus.PENDING));
            stats.setConfirmedReservations(reservationService.countByRestaurantIdAndStatus(restaurantId, ReservationStatus.CONFIRMED));
            stats.setTodayReservations(reservationService.countByRestaurantIdAndDate(restaurantId, LocalDate.now()));
        }
        
        return stats;
    }
}
