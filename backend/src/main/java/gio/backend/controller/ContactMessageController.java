package gio.backend.controller;

import gio.backend.entity.ContactMessage;
import gio.backend.service.ContactMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact-messages")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Contact Messages", description = "Contact messages management")
public class ContactMessageController {

    private final ContactMessageService contactMessageService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get all contact messages", description = "ADMIN only")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mesaje returnată cu succes"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        return ResponseEntity.ok(contactMessageService.getAllMessages());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get contact message by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Mesaj găsit"),
            @ApiResponse(responseCode = "404", description = "Mesajul nu a fost găsit"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<ContactMessage> getMessageById(
            @Parameter(description = "Message ID")
            @PathVariable Integer id) {
        return ResponseEntity.ok(contactMessageService.getMessageById(id));
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get messages by restaurant")
    public ResponseEntity<List<ContactMessage>> getMessagesByRestaurant(
            @Parameter(description = "Restaurant ID")
            @PathVariable Integer restaurantId) {
        return ResponseEntity.ok(contactMessageService.getMessagesByRestaurant(restaurantId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Get pending messages", description = "System admin only")
    public ResponseEntity<List<ContactMessage>> getPendingMessages() {
        return ResponseEntity.ok(contactMessageService.getPendingMessages());
    }

    @PostMapping
    @Operation(summary = "Create contact message", description = "Public endpoint - no auth required")
    public ResponseEntity<ContactMessage> createMessage(
            @Parameter(description = "Contact message data")
            @RequestBody ContactMessage message) {
        ContactMessage created = contactMessageService.createMessage(message);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Update message status and add reply")
    public ResponseEntity<ContactMessage> updateMessageStatus(
            @Parameter(description = "Message ID")
            @PathVariable Integer id,
            
            @Parameter(description = "New status (NEW, PENDING, RESOLVED, CLOSED)")
            @RequestParam String status,
            
            @Parameter(description = "Admin reply message")
            @RequestParam(required = false) String adminReply) {
        ContactMessage updated = contactMessageService.updateMessageStatus(id, status, adminReply);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Delete contact message", description = "System admin only")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "Message ID")
            @PathVariable Integer id) {
        contactMessageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
