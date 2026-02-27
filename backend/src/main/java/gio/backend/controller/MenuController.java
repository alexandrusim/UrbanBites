package gio.backend.controller;

import gio.backend.dto.MenuDTO;
import gio.backend.service.MenuService;
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
@RequestMapping("/api/menus")
@RequiredArgsConstructor
@Tag(name = "Meniuri", description = "API pentru gestionarea meniurilor")
public class MenuController {

    private final MenuService menuService;

    @Operation(
            summary = "Listează toate meniurile",
            description = "Returnează lista tuturor meniurilor din sistem. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de meniuri returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuDTO.class))),
            @ApiResponse(responseCode = "401", description = "Neautorizat - token lipsă sau invalid", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<List<MenuDTO>> getAllMenus() {
        List<MenuDTO> menus = menuService.getAllMenus();
        return ResponseEntity.ok(menus);
    }

    @Operation(
            summary = "Obține un meniu după ID",
            description = "Returnează detaliile unui meniu specific după ID. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meniu găsit",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuDTO.class))),
            @ApiResponse(responseCode = "404", description = "Meniul nu a fost găsit", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> getMenuById(
            @Parameter(description = "ID-ul meniului", required = true, example = "1")
            @PathVariable Integer id) {
        MenuDTO menu = menuService.getMenuById(id);
        return ResponseEntity.ok(menu);
    }

    @Operation(
            summary = "Obține meniurile unui restaurant",
            description = "Returnează toate meniurile asociate unui restaurant specific. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de meniuri returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuDTO.class))),
            @ApiResponse(responseCode = "404", description = "Restaurantul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<MenuDTO>> getMenusByRestaurantId(
            @Parameter(description = "ID-ul restaurantului", required = true, example = "1")
            @PathVariable Integer restaurantId) {
        List<MenuDTO> menus = menuService.getMenusByRestaurantId(restaurantId);
        return ResponseEntity.ok(menus);
    }

    @Operation(
            summary = "Obține meniurile active ale unui restaurant",
            description = "Returnează doar meniurile active ale unui restaurant. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de meniuri active returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuDTO.class))),
            @ApiResponse(responseCode = "404", description = "Restaurantul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/restaurant/{restaurantId}/active")
    public ResponseEntity<List<MenuDTO>> getActiveMenusByRestaurantId(
            @Parameter(description = "ID-ul restaurantului", required = true, example = "1")
            @PathVariable Integer restaurantId) {
        List<MenuDTO> menus = menuService.getActiveMenusByRestaurantId(restaurantId);
        return ResponseEntity.ok(menus);
    }

    @Operation(
            summary = "Creează un meniu nou",
            description = "Creează un meniu nou pentru un restaurant. Accesibil doar pentru administratori de restaurant și administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Meniu creat cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuDTO.class))),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis - rol insuficient", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<MenuDTO> createMenu(
            @Parameter(description = "Datele meniului", required = true)
            @Valid @RequestBody MenuDTO menuDTO) {
        MenuDTO createdMenu = menuService.createMenu(menuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenu);
    }

    @Operation(
            summary = "Actualizează un meniu",
            description = "Actualizează datele unui meniu existent. Accesibil doar pentru administratori de restaurant și administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meniu actualizat cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuDTO.class))),
            @ApiResponse(responseCode = "404", description = "Meniul nu a fost găsit", content = @Content),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuDTO> updateMenu(
            @Parameter(description = "ID-ul meniului", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Datele actualizate ale meniului", required = true)
            @Valid @RequestBody MenuDTO menuDTO) {
        MenuDTO updatedMenu = menuService.updateMenu(id, menuDTO);
        return ResponseEntity.ok(updatedMenu);
    }

    @Operation(
            summary = "Șterge un meniu",
            description = "Șterge un meniu din sistem. Accesibil doar pentru administratori de restaurant și administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Meniu șters cu succes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Meniul nu a fost găsit", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(
            @Parameter(description = "ID-ul meniului", required = true, example = "1")
            @PathVariable Integer id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }
}
