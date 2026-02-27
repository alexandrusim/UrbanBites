package gio.backend.controller;

import gio.backend.dto.GuestPaymentDTO;
import gio.backend.dto.PaymentDTO;
import gio.backend.entity.Payment;
import gio.backend.entity.Reservation;
import gio.backend.repository.PaymentRepository;
import gio.backend.repository.ReservationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/guest/payments")
@RequiredArgsConstructor
@Tag(name = "Guest Payments", description = "API pentru plăți guest (fără autentificare)")
public class GuestPaymentController {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Operation(
            summary = "Creează o plată pentru o rezervare guest",
            description = "Permite utilizatorilor neautentificați să facă plăți pentru rezervările lor. Nu necesită autentificare."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plată creată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "404", description = "Rezervarea nu există", content = @Content)
    })
    @PostMapping
    public ResponseEntity<?> createGuestPayment(
            @Parameter(description = "Detaliile plății guest", required = true)
            @Valid @RequestBody GuestPaymentDTO guestPaymentDTO) {
        try {
            System.out.println("=== GUEST PAYMENT REQUEST ===");
            System.out.println("Data: " + guestPaymentDTO);

            // Verify reservation exists
            Reservation reservation = reservationRepository.findById(guestPaymentDTO.getReservationId())
                    .orElseThrow(() -> new RuntimeException("Rezervarea nu există"));

            // Create payment entity
            Payment payment = new Payment();
            payment.setReservationId(guestPaymentDTO.getReservationId());
            payment.setUserId(null); // Guest payment - no user ID
            payment.setAmount(guestPaymentDTO.getAmount());
            payment.setCurrency(guestPaymentDTO.getCurrency());
            payment.setPaymentMethod(guestPaymentDTO.getPaymentMethod());
            payment.setPaymentStatus("PENDING");
            payment.setPaymentProvider(guestPaymentDTO.getPaymentProvider());

            // Generate transaction ID
            String transactionId = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.setTransactionId(transactionId);

            // Simulate payment processing
            // In production, this would integrate with real payment gateway
            if ("CARD".equals(guestPaymentDTO.getPaymentMethod())) {
                // Simulate card payment processing
                boolean paymentSuccess = simulateCardPayment(guestPaymentDTO);
                if (paymentSuccess) {
                    payment.setPaymentStatus("COMPLETED");
                    payment.setPaidAt(LocalDateTime.now());
                } else {
                    payment.setPaymentStatus("FAILED");
                }
            } else if ("CASH".equals(guestPaymentDTO.getPaymentMethod())) {
                // Cash payment stays PENDING until confirmed by restaurant
                payment.setPaymentStatus("PENDING");
            }

            Payment savedPayment = paymentRepository.save(payment);
            System.out.println("Success! Created payment: " + savedPayment.getPaymentId());

            // Convert to DTO
            PaymentDTO responseDTO = convertToDTO(savedPayment);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);

        } catch (RuntimeException e) {
            System.err.println("=== ERROR CREATING GUEST PAYMENT ===");
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", e.getMessage(), "type", e.getClass().getSimpleName()));
        }
    }

    @Operation(
            summary = "Verifică statusul unei plăți după transaction ID",
            description = "Permite verificarea statusului plății folosind transaction ID-ul primit la creare. Public, fără autentificare."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plată găsită",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plata nu a fost găsită", content = @Content)
    })
    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentDTO> getPaymentByTransactionId(
            @Parameter(description = "Transaction ID-ul", required = true, example = "TRX-ABC123XY")
            @PathVariable String transactionId) {
        try {
            Payment payment = paymentRepository.findByTransactionId(transactionId)
                    .orElseThrow(() -> new RuntimeException("Plata nu a fost găsită"));
            PaymentDTO dto = convertToDTO(payment);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Obține plățile pentru o rezervare",
            description = "Returnează toate plățile asociate unei rezervări. Public, fără autentificare."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listă plăți returnată",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rezervarea nu există", content = @Content)
    })
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<?> getPaymentsByReservationId(
            @Parameter(description = "ID-ul rezervării", required = true, example = "15")
            @PathVariable Integer reservationId) {
        try {
            var payments = paymentRepository.findByReservationId(reservationId);
            var dtos = payments.stream().map(this::convertToDTO).toList();
            return ResponseEntity.ok(dtos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Helper methods
    private boolean simulateCardPayment(GuestPaymentDTO paymentDTO) {
        // Simulate payment gateway processing
        // In production, integrate with Stripe, PayPal, etc.

        // Simple validation - accept all cards except those ending in 0000
        if (paymentDTO.getCardNumber() != null && paymentDTO.getCardNumber().endsWith("0000")) {
            return false; // Simulate failed payment
        }

        return true; // Simulate successful payment
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
        dto.setPaidAt(payment.getPaidAt());
        dto.setRefundedAt(payment.getRefundedAt());
        dto.setRefundAmount(payment.getRefundAmount());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }
}
