package gio.backend.controller;

import gio.backend.dto.NotificationDTO;
import gio.backend.service.NotificationService;
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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificări", description = "API pentru gestionarea notificărilor")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(
            summary = "Listează toate notificările",
            description = "Returnează lista tuturor notificărilor din sistem. Accesibil doar pentru administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificări returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Obține o notificare după ID",
            description = "Returnează detaliile unei notificări specifice. Accesibil doar pentru proprietarul notificării sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificare găsită",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notificarea nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationDTO> getNotificationById(
            @Parameter(description = "ID-ul notificării", required = true, example = "1")
            @PathVariable Integer id) {
        NotificationDTO notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @Operation(
            summary = "Obține notificările unui utilizator",
            description = "Returnează toate notificările unui utilizator specific. Accesibil pentru utilizatorul însuși sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificări returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByUserId(
            @Parameter(description = "ID-ul utilizatorului", required = true, example = "1")
            @PathVariable Integer userId) {
        List<NotificationDTO> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Obține notificările necitite ale unui utilizator",
            description = "Returnează doar notificările necitite ale unui utilizator. Accesibil pentru utilizatorul însuși sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de notificări necitite returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotificationsByUserId(
            @Parameter(description = "ID-ul utilizatorului", required = true, example = "1")
            @PathVariable Integer userId) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    @Operation(
            summary = "Creează o notificare nouă",
            description = "Trimite o notificare nouă către un utilizator. Accesibil doar pentru administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Notificare creată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @Parameter(description = "Datele notificării", required = true)
            @Valid @RequestBody NotificationDTO notificationDTO) {
        NotificationDTO createdNotification = notificationService.createNotification(notificationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNotification);
    }

    @Operation(
            summary = "Marchează o notificare ca citită",
            description = "Actualizează statusul unei notificări la citită. Accesibil pentru proprietarul notificării sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notificare marcată ca citită cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = NotificationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Notificarea nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(
            @Parameter(description = "ID-ul notificării", required = true, example = "1")
            @PathVariable Integer id) {
        NotificationDTO updatedNotification = notificationService.markAsRead(id);
        return ResponseEntity.ok(updatedNotification);
    }

    @Operation(
            summary = "Marchează toate notificările ca citite",
            description = "Marchează toate notificările unui utilizator ca citite. Accesibil pentru utilizatorul însuși sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Toate notificările au fost marcate ca citite", content = @Content),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsReadForUser(
            @Parameter(description = "ID-ul utilizatorului", required = true, example = "1")
            @PathVariable Integer userId) {
        notificationService.markAllAsReadForUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Șterge o notificare",
            description = "Șterge o notificare din sistem. Accesibil pentru proprietarul notificării sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Notificare ștearsă cu succes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Notificarea nu a fost găsită", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "ID-ul notificării", required = true, example = "1")
            @PathVariable Integer id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Șterge toate notificările unui utilizator",
            description = "Șterge toate notificările asociate unui utilizator. Accesibil pentru utilizatorul însuși sau administratori."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Toate notificările au fost șterse", content = @Content),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllForUser(
            @Parameter(description = "ID-ul utilizatorului", required = true, example = "1")
            @PathVariable Integer userId) {
        notificationService.deleteAllForUser(userId);
        return ResponseEntity.noContent().build();
    }
}
