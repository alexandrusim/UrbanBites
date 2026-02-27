package gio.backend.controller;

import gio.backend.dto.PaymentDTO;
import gio.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Plăți", description = "API pentru gestionarea plăților")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Listează toate plățile",
            description = "Returnează lista tuturor plăților din sistem. Accesibil doar pentru administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de plăți returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @Operation(
            summary = "Obține o plată după ID",
            description = "Returnează detaliile unei plăți specifice. Accesibil pentru proprietarul plății sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plată găsită",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plata nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentDTO> getPaymentById(
            @Parameter(description = "ID-ul plății", required = true, example = "1")
            @PathVariable Integer id) {
        PaymentDTO payment = paymentService.getPaymentById(id);
        return ResponseEntity.ok(payment);
    }

    @Operation(
            summary = "Obține plățile pentru o rezervare",
            description = "Returnează toate plățile asociate unei rezervări specifice. Accesibil pentru proprietarul rezervării sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de plăți returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rezervarea nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByReservationId(
            @Parameter(description = "ID-ul rezervării", required = true, example = "1")
            @PathVariable Integer reservationId) {
        List<PaymentDTO> payments = paymentService.getPaymentsByReservationId(reservationId);
        return ResponseEntity.ok(payments);
    }

    @Operation(
            summary = "Obține plățile după status",
            description = "Returnează toate plățile cu un status specific. Accesibil doar pentru administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de plăți returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByStatus(
            @Parameter(description = "Statusul plății", required = true, example = "COMPLETED")
            @PathVariable String status) {
        List<PaymentDTO> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @Operation(
            summary = "Creează o plată nouă",
            description = "Inițiază o plată nouă pentru o rezervare. Accesibil pentru proprietarul rezervării sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Plată creată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    public ResponseEntity<PaymentDTO> createPayment(
            @Parameter(description = "Datele plății", required = true)
            @Valid @RequestBody PaymentDTO paymentDTO) {
        PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayment);
    }

    @Operation(
            summary = "Procesează o plată",
            description = "Marchează o plată ca procesată și completă. Accesibil doar pentru administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plată procesată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plata nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PutMapping("/{id}/process")
    public ResponseEntity<PaymentDTO> processPayment(
            @Parameter(description = "ID-ul plății", required = true, example = "1")
            @PathVariable Integer id) {
        PaymentDTO processedPayment = paymentService.processPayment(id);
        return ResponseEntity.ok(processedPayment);
    }

    @Operation(
            summary = "Actualizează statusul unei plăți",
            description = "Actualizează statusul unei plăți. Accesibil doar pentru administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status actualizat cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PaymentDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plata nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(
            @Parameter(description = "ID-ul plății", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Noul status al plății", required = true, example = "COMPLETED")
            @RequestParam String status) {
        PaymentDTO updatedPayment = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(updatedPayment);
    }

    @Operation(
            summary = "Șterge o plată",
            description = "Șterge o plată din sistem. Accesibil doar pentru administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Plată ștearsă cu succes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Plata nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(
            @Parameter(description = "ID-ul plății", required = true, example = "1")
            @PathVariable Integer id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}
