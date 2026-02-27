package gio.backend.controller;

import gio.backend.entity.User;
import gio.backend.enums.UserRole;
import gio.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Utilizatori", description = "API pentru gestionarea utilizatorilor")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Listează toți utilizatorii", description = "Doar pentru SYSTEM_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de utilizatori returnată"),
            @ApiResponse(responseCode = "403", description = "Doar SYSTEM_ADMIN"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Obține utilizator după ID")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilizator găsit"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(
            @Parameter(description = "ID-ul utilizatorului", required = true)
            @PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obține utilizator după email")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilizator găsit"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<User> getUserByEmail(
            @Parameter(description = "Email-ul utilizatorului", required = true)
            @PathVariable String email) {
        try {
            User user = userService.getUserByEmail(email);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Verifică dacă email-ul există")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verificarea completă"),
            @ApiResponse(responseCode = "500", description = "Eroare server")
    })
    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Boolean> checkEmailExists(
            @Parameter(description = "Email-ul de verificat", required = true)
            @PathVariable String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }

    @Operation(summary = "Actualizează utilizator")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilizator actualizat cu succes"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @Parameter(description = "ID-ul utilizatorului", required = true)
            @PathVariable Integer id, 
            @RequestBody User user) {
        try {
            User updated = userService.updateUser(id, user);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Șterge utilizator", description = "Doar pentru SYSTEM_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilizator șters cu succes"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "403", description = "Doar SYSTEM_ADMIN"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID-ul utilizatorului", required = true)
            @PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizează rolul utilizatorului", description = "Doar pentru SYSTEM_ADMIN")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Rol actualizat cu succes"),
            @ApiResponse(responseCode = "404", description = "Utilizatorul nu a fost găsit"),
            @ApiResponse(responseCode = "403", description = "Doar SYSTEM_ADMIN"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    @PutMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(
            @Parameter(description = "ID-ul utilizatorului", required = true)
            @PathVariable Integer id, 
            @Parameter(description = "Noul rol", required = true)
            @RequestParam UserRole role) {
        try {
            User updated = userService.updateUserRole(id, role);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
