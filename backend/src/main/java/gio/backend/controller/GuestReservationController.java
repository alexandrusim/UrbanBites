package gio.backend.controller;

import gio.backend.dto.GuestReservationDTO;
import gio.backend.dto.ReservationApprovalDTO;
import gio.backend.dto.ReservationDetailDTO;
import gio.backend.service.ReservationService;
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
@RequestMapping("/api/guest")
@RequiredArgsConstructor
@Tag(name = "Rezervări Guest", description = "API pentru rezervări fără cont (utilizatori neautentificați)")
public class GuestReservationController {

    private final ReservationService reservationService;

    @Operation(
            summary = "Creează o rezervare fără cont",
            description = "Permite utilizatorilor neautentificați să facă rezervări. Rezervarea va avea status PENDING și va trebui aprobată de un admin."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rezervare creată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDetailDTO.class))),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "404", description = "Restaurantul nu există", content = @Content)
    })
    @PostMapping("/reservations")
    public ResponseEntity<?> createGuestReservation(
            @Parameter(description = "Detaliile rezervării guest", required = true)
            @Valid @RequestBody GuestReservationDTO guestReservationDTO) {
        try {
            System.out.println("=== GUEST RESERVATION REQUEST ===");
            System.out.println("Data: " + guestReservationDTO);
            ReservationDetailDTO created = reservationService.createGuestReservation(guestReservationDTO);
            System.out.println("Success! Created reservation: " + created.getReservationId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            System.err.println("=== ERROR CREATING GUEST RESERVATION ===");
            e.printStackTrace();
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage(), "type", e.getClass().getSimpleName()));
        }
    }

    @Operation(
            summary = "Verifică statusul rezervării după codul de confirmare",
            description = "Permite verificarea statusului rezervării folosind codul de confirmare primit la creare. Public, fără autentificare."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rezervare găsită",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDetailDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rezervarea nu a fost găsită", content = @Content)
    })
    @GetMapping("/reservations/confirmation/{code}")
    public ResponseEntity<ReservationDetailDTO> getReservationByConfirmationCode(
            @Parameter(description = "Codul de confirmare", required = true, example = "ABC123XY")
            @PathVariable String code) {
        try {
            ReservationDetailDTO reservation = reservationService.getReservationDetailByConfirmationCode(code);
            return ResponseEntity.ok(reservation);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Obține rezervările în așteptare pentru un restaurant",
            description = "Returnează toate rezervările cu status PENDING pentru un restaurant. Accesibil doar pentru ADMIN_RESTAURANT și SYSTEM_ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de rezervări returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDetailDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @GetMapping("/reservations/restaurant/{restaurantId}/pending")
    public ResponseEntity<List<ReservationDetailDTO>> getPendingReservations(
            @Parameter(description = "ID-ul restaurantului", required = true, example = "1")
            @PathVariable Integer restaurantId) {
        List<ReservationDetailDTO> reservations = reservationService.getPendingReservationsForRestaurant(restaurantId);
        return ResponseEntity.ok(reservations);
    }

    @Operation(
            summary = "Aprobă sau respinge o rezervare",
            description = "Permite adminilor să aprobe (CONFIRMED), să respingă (CANCELLED) sau să actualizeze o rezervare. Poate asigna și o masă."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rezervare actualizată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationDetailDTO.class))),
            @ApiResponse(responseCode = "404", description = "Rezervarea nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PutMapping("/reservations/approve")
    public ResponseEntity<ReservationDetailDTO> approveReservation(
            @Parameter(description = "Detaliile aprobării", required = true)
            @Valid @RequestBody ReservationApprovalDTO approvalDTO) {
        try {
            ReservationDetailDTO updated = reservationService.approveReservation(approvalDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
