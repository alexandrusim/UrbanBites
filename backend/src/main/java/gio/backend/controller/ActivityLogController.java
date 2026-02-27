package gio.backend.controller;

import gio.backend.entity.ActivityLog;
import gio.backend.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
@Tag(name = "Activity Logs", description = "User activity tracking endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Get all activity logs", 
               description = "Returns last 100 activity logs (SYSTEM_ADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de activity logs returnată cu succes"),
            @ApiResponse(responseCode = "403", description = "Doar SYSTEM_ADMIN poate accesa"),
            @ApiResponse(responseCode = "401", description = "Neautorizat")
    })
    public ResponseEntity<List<ActivityLog>> getAllLogs() {
        List<ActivityLog> logs = activityLogService.getAllLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Get activity logs by user ID", 
               description = "Returns all activity logs for a specific user (SYSTEM_ADMIN only)")
    public ResponseEntity<List<ActivityLog>> getLogsByUser(
            @Parameter(description = "User ID")
            @PathVariable Integer userId) {
        List<ActivityLog> logs = activityLogService.getLogsByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/range")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @Operation(summary = "Get activity logs by date range", 
               description = "Returns activity logs within a date range (SYSTEM_ADMIN only)")
    public ResponseEntity<List<ActivityLog>> getLogsByDateRange(
            @Parameter(description = "Start datetime")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            
            @Parameter(description = "End datetime")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ActivityLog> logs = activityLogService.getLogsByDateRange(startDate, endDate);
        return ResponseEntity.ok(logs);
    }
}
