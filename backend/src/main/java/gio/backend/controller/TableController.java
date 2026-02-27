package gio.backend.controller;

import gio.backend.entity.Table;
import gio.backend.service.TableService;
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
@RequestMapping("/api/tables")
@Tag(name = "Mese", description = "API pentru gestionarea meselor")
public class TableController {

    @Autowired
    private TableService tableService;

    @GetMapping
    @Operation(summary = "Listează toate mesele")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mese returnată cu succes"),
            @ApiResponse(responseCode = "500", description = "Eroare server")
    })
    public ResponseEntity<List<Table>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obține masă după ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Masă găsită"),
            @ApiResponse(responseCode = "404", description = "Masa nu a fost găsită")
    })
    public ResponseEntity<Table> getTableById(
            @Parameter(description = "ID-ul mesei", required = true)
            @PathVariable Integer id) {
        return tableService.getTableById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Obține mese după restaurant")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mese returnată"),
            @ApiResponse(responseCode = "404", description = "Restaurant nu a fost găsit")
    })
    public ResponseEntity<List<Table>> getTablesByRestaurantId(
            @Parameter(description = "ID-ul restaurantului", required = true)
            @PathVariable Integer restaurantId) {
        return ResponseEntity.ok(tableService.getTablesByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/available")
    @Operation(summary = "Obține mese disponibile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de mese disponibile returnată"),
            @ApiResponse(responseCode = "404", description = "Restaurant nu a fost găsit")
    })
    public ResponseEntity<List<Table>> getAvailableTablesByRestaurant(
            @Parameter(description = "ID-ul restaurantului", required = true)
            @PathVariable Integer restaurantId) {
        return ResponseEntity.ok(tableService.getAvailableTablesByRestaurant(restaurantId));
    }

    @PostMapping
    @Operation(summary = "Creează masă nouă")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Masă creată cu succes"),
            @ApiResponse(responseCode = "400", description = "Date invalide"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<Table> createTable(@RequestBody Table table) {
        try {
            Table created = tableService.createTable(table);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizează masă")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Masă actualizată cu succes"),
            @ApiResponse(responseCode = "404", description = "Masa nu a fost găsită"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<Table> updateTable(
            @Parameter(description = "ID-ul mesei", required = true)
            @PathVariable Integer id,
            @RequestBody Table table) {
        try {
            Table updated = tableService.updateTable(id, table);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Șterge masă")
    @SecurityRequirement(name = "Bearer Authentication")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Masă ștearsă cu succes"),
            @ApiResponse(responseCode = "404", description = "Masa nu a fost găsită"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<Void> deleteTable(
            @Parameter(description = "ID-ul mesei", required = true)
            @PathVariable Integer id) {
        try {
            tableService.deleteTable(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
