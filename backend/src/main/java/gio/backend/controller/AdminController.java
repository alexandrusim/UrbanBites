package gio.backend.controller;

import gio.backend.dto.DashboardStatsDTO;
import gio.backend.dto.ReservationDTO;
import gio.backend.dto.ReservationStatusUpdateDTO;
import gio.backend.dto.UserDTO;
import gio.backend.entity.User;
import gio.backend.enums.ReservationStatus;
import gio.backend.service.AdminService;
import gio.backend.service.ReservationService;
import gio.backend.service.SecurityService;
import gio.backend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Admin", description = "Admin management endpoints")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        DashboardStatsDTO stats = adminService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsersDTO();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.getUserDTOById(id);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationDTO>> getAllReservations(
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(required = false) LocalDate date) {
        
        User currentUser = securityService.getCurrentUser();
        List<ReservationDTO> reservations;
        
        if (securityService.isSystemAdmin()) {
            if (status != null) {
                reservations = reservationService.getReservationsByStatus(status);
            } else if (date != null) {
                reservations = reservationService.getReservationsByDate(date);
            } else {
                reservations = reservationService.getAllReservationsDTO();
            }
        } else if (securityService.isRestaurantAdmin() && currentUser.getRestaurantId() != null) {
            Integer restaurantId = currentUser.getRestaurantId();
            reservations = reservationService.getReservationsByRestaurantIdDTO(restaurantId);
            
            if (status != null) {
                reservations = reservations.stream()
                        .filter(r -> r.getStatus() == status)
                        .collect(Collectors.toList());
            }
            if (date != null) {
                reservations = reservations.stream()
                        .filter(r -> r.getReservationDate().equals(date))
                        .collect(Collectors.toList());
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(reservations);
    }

    @PutMapping("/reservations/{id}/status")
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @PathVariable Integer id,
            @RequestBody ReservationStatusUpdateDTO statusUpdate) {
        
        User currentUser = securityService.getCurrentUser();
        
        if (securityService.isRestaurantAdmin()) {
            ReservationDTO reservation = reservationService.getAllReservationsDTO().stream()
                    .filter(r -> r.getReservationId().equals(id))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Reservation not found"));
            
            if (!reservation.getRestaurantId().equals(currentUser.getRestaurantId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        
        ReservationDTO updated = reservationService.updateReservationStatus(id, statusUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/reservations/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }
}
