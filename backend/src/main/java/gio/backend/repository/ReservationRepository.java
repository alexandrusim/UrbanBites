package gio.backend.repository;

import gio.backend.entity.Reservation;
import gio.backend.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByUserId(Integer userId);
    List<Reservation> findByRestaurantId(Integer restaurantId);
    List<Reservation> findByTableId(Integer tableId);
    Optional<Reservation> findByConfirmationCode(String confirmationCode);
    List<Reservation> findByStatus(ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId AND r.reservationDate = :date")
    List<Reservation> findByRestaurantIdAndDate(@Param("restaurantId") Integer restaurantId, @Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.tableId = :tableId AND r.reservationDate = :date AND r.status IN ('PENDING', 'CONFIRMED')")
    List<Reservation> findActiveReservationsByTableAndDate(@Param("tableId") Integer tableId, @Param("date") LocalDate date);
    
    long countByStatus(ReservationStatus status);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.reservationDate = :date")
    long countByDate(@Param("date") LocalDate date);
    
    @Query("SELECT r FROM Reservation r WHERE r.reservationDate = :date")
    List<Reservation> findByDate(@Param("date") LocalDate date);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurantId = :restaurantId")
    long countByRestaurantId(@Param("restaurantId") Integer restaurantId);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurantId = :restaurantId AND r.status = :status")
    long countByRestaurantIdAndStatus(@Param("restaurantId") Integer restaurantId, @Param("status") ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.restaurantId = :restaurantId AND r.status = :status")
    List<Reservation> findByRestaurantIdAndStatus(@Param("restaurantId") Integer restaurantId, @Param("status") ReservationStatus status);
    
    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.restaurantId = :restaurantId AND r.reservationDate = :date")
    long countByRestaurantIdAndDate(@Param("restaurantId") Integer restaurantId, @Param("date") LocalDate date);
}
