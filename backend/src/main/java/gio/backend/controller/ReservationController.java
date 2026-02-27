package gio.backend.controller;

import gio.backend.entity.Reservation;
import gio.backend.enums.ReservationStatus;
import gio.backend.service.ReservationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@Tag(name = "Rezervări", description = "API pentru gestionarea rezervărilor")
@SecurityRequirement(name = "Bearer Authentication")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Integer id) {
        return reservationService.getReservationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/confirmation/{code}")
    public ResponseEntity<Reservation> getReservationByConfirmationCode(@PathVariable String code) {
        return reservationService.getReservationByConfirmationCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Reservation>> getReservationsByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(reservationService.getReservationsByUserId(userId));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Reservation>> getReservationsByRestaurantId(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(reservationService.getReservationsByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/date/{date}")
    public ResponseEntity<List<Reservation>> getReservationsByRestaurantAndDate(
            @PathVariable Integer restaurantId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reservationService.getReservationsByRestaurantAndDate(restaurantId, date));
    }

    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Reservation>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        return ResponseEntity.ok(reservationService.getReservationsByStatusEntity(status));
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        try {
            Reservation created = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Integer id,
            @RequestBody Reservation reservation) {
        try {
            Reservation updated = reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Integer id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/table/{tableId}/date/{date}/available")
    public ResponseEntity<Boolean> checkTableAvailability(
            @PathVariable Integer tableId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        boolean available = reservationService.checkTableAvailability(tableId, date);
        return ResponseEntity.ok(available);
    }
}
