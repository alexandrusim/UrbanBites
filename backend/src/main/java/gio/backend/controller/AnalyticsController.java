package gio.backend.controller;

import gio.backend.dto.AnalyticsReportDTO;
import gio.backend.dto.PeakHoursDTO;
import gio.backend.dto.PopularMenuItemDTO;
import gio.backend.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Analytics and reporting endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/reports")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get analytics report for period", 
               description = "Restaurant admins can only access their own restaurant data. System admins can access all data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Raportul analitic a fost generat cu succes"),
            @ApiResponse(responseCode = "400", description = "Date invalide - verifică startDate și endDate"),
            @ApiResponse(responseCode = "403", description = "Acces interzis - nu poți accesa datele acestui restaurant"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<AnalyticsReportDTO> getReport(
            @Parameter(description = "Start date of the report period")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date of the report period")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Restaurant ID (optional for system admins)")
            @RequestParam(required = false) Integer restaurantId) {
        
        AnalyticsReportDTO report = analyticsService.getReportForPeriod(startDate, endDate, restaurantId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/export")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Export analytics report", 
               description = "Export report as CSV or JSON format")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Raportul a fost exportat cu succes"),
            @ApiResponse(responseCode = "400", description = "Format invalid sau date incomplete"),
            @ApiResponse(responseCode = "403", description = "Acces interzis"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<String> exportReport(
            @Parameter(description = "Export format: CSV or JSON")
            @RequestParam(defaultValue = "CSV") String format,
            
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Restaurant ID")
            @RequestParam(required = false) Integer restaurantId) {
        
        AnalyticsReportDTO report = analyticsService.getReportForPeriod(startDate, endDate, restaurantId);
        
        if ("CSV".equalsIgnoreCase(format)) {
            String csv = analyticsService.exportReportAsCSV(report);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "analytics-report.csv");
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv);
        } else {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(report.toString());
        }
    }

    @GetMapping("/top-menus")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get top popular menu items", 
               description = "Returns most ordered menu items")
    public ResponseEntity<List<PopularMenuItemDTO>> getTopMenuItems(
            @Parameter(description = "Restaurant ID")
            @RequestParam(required = false) Integer restaurantId,
            
            @Parameter(description = "Maximum number of items to return")
            @RequestParam(defaultValue = "10") int limit) {
        
        List<PopularMenuItemDTO> topItems = analyticsService.getTopMenuItems(restaurantId, limit);
        return ResponseEntity.ok(topItems);
    }

    @GetMapping("/peak-hours")
    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @Operation(summary = "Get peak reservation hours", 
               description = "Returns hours with most reservations")
    public ResponseEntity<List<PeakHoursDTO>> getPeakHours(
            @Parameter(description = "Restaurant ID")
            @RequestParam(required = false) Integer restaurantId,
            
            @Parameter(description = "Start date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<PeakHoursDTO> peakHours = analyticsService.getPeakHours(restaurantId, startDate, endDate);
        return ResponseEntity.ok(peakHours);
    }
}
