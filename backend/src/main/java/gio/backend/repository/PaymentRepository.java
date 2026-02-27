package gio.backend.repository;

import gio.backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByReservationId(Integer reservationId);
    List<Payment> findByPaymentStatus(String paymentStatus);
    Optional<Payment> findByTransactionId(String transactionId);
}
