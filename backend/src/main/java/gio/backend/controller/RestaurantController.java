package gio.backend.controller;

import gio.backend.entity.Restaurant;
import gio.backend.service.RestaurantService;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/restaurants")
@Tag(name = "Restaurante", description = "API pentru gestionarea restaurantelor")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;



    @GetMapping
    @Operation(summary = "Listează toate restaurantele")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de restaurante returnată cu succes"),
            @ApiResponse(responseCode = "500", description = "Eroare server")
    })
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obține restaurant după ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant găsit"),
            @ApiResponse(responseCode = "404", description = "Restaurant nu a fost găsit")
    })
    public ResponseEntity<Restaurant> getRestaurantById(
            @Parameter(description = "ID-ul restaurantului", required = true, example = "1")
            @PathVariable Integer id) {
        return restaurantService.getRestaurantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city/{city}")
    @Operation(summary = "Obține restaurante după oraș")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de restaurante returnată"),
            @ApiResponse(responseCode = "404", description = "Niciun restaurant găsit")
    })
    public ResponseEntity<List<Restaurant>> getRestaurantsByCity(
            @Parameter(description = "Numele orașului", required = true, example = "Brașov")
            @PathVariable String city) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByCity(city));
    }

    @GetMapping("/cuisine/{cuisineType}")
    @Operation(summary = "Obține restaurante după tip de bucătărie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de restaurante returnată"),
            @ApiResponse(responseCode = "404", description = "Niciun restaurant găsit")
    })
    public ResponseEntity<List<Restaurant>> getRestaurantsByCuisineType(
            @Parameter(description = "Tipul de bucătărie", required = true, example = "Italian")
            @PathVariable String cuisineType) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByCuisineType(cuisineType));
    }

    @GetMapping("/active")
    @Operation(summary = "Obține restaurante active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de restaurante active returnată"),
            @ApiResponse(responseCode = "404", description = "Niciun restaurant activ găsit")
    })
    public ResponseEntity<List<Restaurant>> getActiveRestaurants() {
        return ResponseEntity.ok(restaurantService.getActiveRestaurants());
    }

    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PostMapping
    @Operation(summary = "Creează restaurant nou")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurant creat cu succes"),
            @ApiResponse(responseCode = "400", description = "Date invalide"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<Restaurant> createRestaurant(@RequestBody Restaurant restaurant) {
        try {
            Restaurant created = restaurantService.createRestaurant(restaurant);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Actualizează restaurant")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Restaurant actualizat cu succes"),
            @ApiResponse(responseCode = "404", description = "Restaurant nu a fost găsit"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<Restaurant> updateRestaurant(
            @Parameter(description = "ID-ul restaurantului", required = true)
            @PathVariable Integer id,
            @RequestBody Restaurant restaurant) {
        try {
            Restaurant updated = restaurantService.updateRestaurant(id, restaurant);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Șterge restaurant")
    @SecurityRequirement(name = "Bearer Authentication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Restaurant șters cu succes"),
            @ApiResponse(responseCode = "404", description = "Restaurant nu a fost găsit"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<Void> deleteRestaurant(
            @Parameter(description = "ID-ul restaurantului", required = true)
            @PathVariable Integer id) {
        try {
            restaurantService.deleteRestaurant(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
