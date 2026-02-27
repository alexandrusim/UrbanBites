package gio.backend.controller;

import gio.backend.dto.MenuItemDTO;
import gio.backend.service.MenuItemService;
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
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
@Tag(name = "Produse din meniu", description = "API pentru gestionarea produselor din meniu")
public class MenuItemController {

    private final MenuItemService menuItemService;

    @Operation(
            summary = "Listează toate produsele",
            description = "Returnează lista tuturor produselor din meniu. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produse returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuItemDTO.class))),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItems() {
        List<MenuItemDTO> menuItems = menuItemService.getAllMenuItems();
        return ResponseEntity.ok(menuItems);
    }

    @Operation(
            summary = "Obține un produs după ID",
            description = "Returnează detaliile unui produs specific după ID. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produs găsit",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produsul nu a fost găsit", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(
            @Parameter(description = "ID-ul produsului", required = true, example = "1")
            @PathVariable Integer id) {
        MenuItemDTO menuItem = menuItemService.getMenuItemById(id);
        return ResponseEntity.ok(menuItem);
    }

    @Operation(
            summary = "Obține produsele unui meniu",
            description = "Returnează toate produsele dintr-un meniu specific. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produse returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Meniul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/menu/{menuId}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByMenuId(
            @Parameter(description = "ID-ul meniului", required = true, example = "1")
            @PathVariable Integer menuId) {
        List<MenuItemDTO> menuItems = menuItemService.getMenuItemsByMenuId(menuId);
        return ResponseEntity.ok(menuItems);
    }

    @Operation(
            summary = "Obține produsele disponibile dintr-un meniu",
            description = "Returnează doar produsele disponibile dintr-un meniu specific. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produse disponibile returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Meniul nu există", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/menu/{menuId}/available")
    public ResponseEntity<List<MenuItemDTO>> getAvailableMenuItemsByMenuId(
            @Parameter(description = "ID-ul meniului", required = true, example = "1")
            @PathVariable Integer menuId) {
        List<MenuItemDTO> menuItems = menuItemService.getAvailableMenuItemsByMenuId(menuId);
        return ResponseEntity.ok(menuItems);
    }

    @Operation(
            summary = "Obține produse după categorie",
            description = "Returnează toate produsele dintr-o categorie specifică. Accesibil pentru toți utilizatorii autentificați."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de produse returnată cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuItemDTO.class))),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItemsByCategory(
            @Parameter(description = "Categoria produselor", required = true, example = "Pizza")
            @PathVariable String category) {
        List<MenuItemDTO> menuItems = menuItemService.getMenuItemsByCategory(category);
        return ResponseEntity.ok(menuItems);
    }

    @Operation(
            summary = "Creează un produs nou",
            description = "Adaugă un produs nou în meniu. Accesibil doar pentru administratori de restaurant și administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Produs creat cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuItemDTO.class))),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<MenuItemDTO> createMenuItem(
            @Parameter(description = "Datele produsului", required = true)
            @Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO createdMenuItem = menuItemService.createMenuItem(menuItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMenuItem);
    }

    @Operation(
            summary = "Actualizează un produs",
            description = "Actualizează datele unui produs existent. Accesibil doar pentru administratori de restaurant și administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Produs actualizat cu succes",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MenuItemDTO.class))),
            @ApiResponse(responseCode = "404", description = "Produsul nu a fost găsit", content = @Content),
            @ApiResponse(responseCode = "400", description = "Date invalide", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(
            @Parameter(description = "ID-ul produsului", required = true, example = "1")
            @PathVariable Integer id,
            @Parameter(description = "Datele actualizate ale produsului", required = true)
            @Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO updatedMenuItem = menuItemService.updateMenuItem(id, menuItemDTO);
        return ResponseEntity.ok(updatedMenuItem);
    }

    @Operation(
            summary = "Șterge un produs",
            description = "Șterge un produs din meniu. Accesibil doar pentru administratori de restaurant și administratori de sistem."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Produs șters cu succes", content = @Content),
            @ApiResponse(responseCode = "404", description = "Produsul nu a fost găsit", content = @Content),
            @ApiResponse(responseCode = "403", description = "Acces interzis", content = @Content),
            @ApiResponse(responseCode = "401", description = "Neautorizat", content = @Content)
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(
            @Parameter(description = "ID-ul produsului", required = true, example = "1")
            @PathVariable Integer id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
