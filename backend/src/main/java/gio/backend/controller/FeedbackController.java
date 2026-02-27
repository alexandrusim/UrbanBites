package gio.backend.controller;

import gio.backend.entity.Feedback;
import gio.backend.service.FeedbackService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
@SecurityRequirement(name = "Bearer Authentication")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @PreAuthorize("hasAnyRole('ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Feedback>> getAllFeedback() {
        return ResponseEntity.ok(feedbackService.getAllFeedback());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable Integer id) {
        return feedbackService.getFeedbackById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Feedback>> getFeedbackByRestaurantId(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByRestaurantId(restaurantId));
    }

    @GetMapping("/restaurant/{restaurantId}/visible")
    public ResponseEntity<List<Feedback>> getVisibleFeedbackByRestaurantId(@PathVariable Integer restaurantId) {
        return ResponseEntity.ok(feedbackService.getVisibleFeedbackByRestaurantId(restaurantId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Feedback>> getFeedbackByUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(feedbackService.getFeedbackByUserId(userId));
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'ADMIN_RESTAURANT', 'SYSTEM_ADMIN')")
    @PostMapping
    public ResponseEntity<Feedback> createFeedback(@RequestBody Feedback feedback) {
        try {
            Feedback created = feedbackService.createFeedback(feedback);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> updateFeedback(
            @PathVariable Integer id,
            @RequestBody Feedback feedback) {
        try {
            Feedback updated = feedbackService.updateFeedback(id, feedback);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Integer id) {
        try {
            feedbackService.deleteFeedback(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
