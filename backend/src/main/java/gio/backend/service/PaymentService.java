package gio.backend.service;

import gio.backend.dto.PaymentDTO;
import gio.backend.entity.Payment;
import gio.backend.entity.Reservation;
import gio.backend.entity.Restaurant;
import gio.backend.repository.PaymentRepository;
import gio.backend.repository.ReservationRepository;
import gio.backend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final AsyncNotificationService asyncNotificationService;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentDTO getPaymentById(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plata cu ID " + id + " nu a fost găsită"));
        return convertToDTO(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByReservationId(Integer reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new RuntimeException("Rezervarea cu ID " + reservationId + " nu există");
        }
        
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Rezervarea nu a fost găsită"));
        
        if (!securityService.isSystemAdmin()) {
            if (!securityService.canAccessRestaurant(reservation.getRestaurantId())) {
                securityService.checkUserAccess(reservation.getUserId());
            }
        }
        
        return paymentRepository.findByReservationId(reservationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaymentDTO> getPaymentsByStatus(String status) {
        return paymentRepository.findByPaymentStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        if (!reservationRepository.existsById(paymentDTO.getReservationId())) {
            throw new RuntimeException("Rezervarea cu ID " + paymentDTO.getReservationId() + " nu există");
        }

        Payment payment = convertToEntity(paymentDTO);
        payment.setPaymentStatus("PENDING");
        Payment savedPayment = paymentRepository.save(payment);
        return convertToDTO(savedPayment);
    }

    @Transactional
    public PaymentDTO processPayment(Integer id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plata cu ID " + id + " nu a fost găsită"));

        payment.setPaymentStatus("COMPLETED");
        payment.setPaidAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);
        
        if (updatedPayment.getUserId() != null) {
            try {
                Reservation reservation = reservationRepository.findById(updatedPayment.getReservationId()).orElse(null);
                if (reservation != null) {
                    Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurantId()).orElse(null);
                    String restaurantName = restaurant != null ? restaurant.getName() : "Restaurant";
                    
                    asyncNotificationService.sendPaymentConfirmation(
                        updatedPayment.getUserId(),
                        updatedPayment.getPaymentId(),
                        updatedPayment.getAmount().toString(),
                        restaurantName
                    );
                }
            } catch (Exception e) {
            }
        }
        
        return convertToDTO(updatedPayment);
    }

    @Transactional
    public PaymentDTO updatePaymentStatus(Integer id, String status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plata cu ID " + id + " nu a fost găsită"));

        payment.setPaymentStatus(status);
        if ("COMPLETED".equals(status)) {
            payment.setPaidAt(LocalDateTime.now());
        } else if ("REFUNDED".equals(status)) {
            payment.setRefundedAt(LocalDateTime.now());
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return convertToDTO(updatedPayment);
    }

    @Transactional
    public void deletePayment(Integer id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Plata cu ID " + id + " nu a fost găsită");
        }
        paymentRepository.deleteById(id);
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setReservationId(payment.getReservationId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setTransactionId(payment.getTransactionId());
        dto.setPaymentProvider(payment.getPaymentProvider());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setPaidAt(payment.getPaidAt());
        dto.setProcessedAt(payment.getPaidAt()); 
        dto.setRefundedAt(payment.getRefundedAt());
        dto.setRefundAmount(payment.getRefundAmount());
        return dto;
    }

    private Payment convertToEntity(PaymentDTO dto) {
        Payment payment = new Payment();
        payment.setReservationId(dto.getReservationId());
        payment.setUserId(dto.getUserId());
        payment.setAmount(dto.getAmount());
        payment.setCurrency(dto.getCurrency() != null ? dto.getCurrency() : "RON");
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setPaymentStatus(dto.getPaymentStatus() != null ? dto.getPaymentStatus() : "PENDING");
        payment.setTransactionId(dto.getTransactionId());
        payment.setPaymentProvider(dto.getPaymentProvider());
        return payment;
    }
}
