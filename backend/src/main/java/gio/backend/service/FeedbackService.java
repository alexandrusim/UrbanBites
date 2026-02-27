package gio.backend.service;

import gio.backend.entity.Feedback;
import gio.backend.repository.FeedbackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;
    
    @Autowired
    private SecurityService securityService;

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }

    public Optional<Feedback> getFeedbackById(Integer id) {
        return feedbackRepository.findById(id);
    }

    public List<Feedback> getFeedbackByRestaurantId(Integer restaurantId) {
        return feedbackRepository.findByRestaurantId(restaurantId);
    }

    public List<Feedback> getVisibleFeedbackByRestaurantId(Integer restaurantId) {
        return feedbackRepository.findByRestaurantIdAndIsVisible(restaurantId, true);
    }

    public List<Feedback> getFeedbackByUserId(Integer userId) {
        return feedbackRepository.findByUserId(userId);
    }

    public Feedback createFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

    public Feedback updateFeedback(Integer id, Feedback feedbackDetails) {
        return feedbackRepository.findById(id)
                .map(feedback -> {
                    if (!securityService.isSystemAdmin()) {
                        securityService.checkUserAccess(feedback.getUserId());
                    }
                    
                    if (feedbackDetails.getRating() != null) {
                        feedback.setRating(feedbackDetails.getRating());
                    }
                    if (feedbackDetails.getComment() != null) {
                        feedback.setComment(feedbackDetails.getComment());
                    }
                    if (feedbackDetails.getFoodRating() != null) {
                        feedback.setFoodRating(feedbackDetails.getFoodRating());
                    }
                    if (feedbackDetails.getServiceRating() != null) {
                        feedback.setServiceRating(feedbackDetails.getServiceRating());
                    }
                    if (feedbackDetails.getAmbianceRating() != null) {
                        feedback.setAmbianceRating(feedbackDetails.getAmbianceRating());
                    }
                    if (feedbackDetails.getValueRating() != null) {
                        feedback.setValueRating(feedbackDetails.getValueRating());
                    }
                    if (feedbackDetails.getIsVisible() != null) {
                        feedback.setIsVisible(feedbackDetails.getIsVisible());
                    }
                    if (feedbackDetails.getAdminResponse() != null) {
                        feedback.setAdminResponse(feedbackDetails.getAdminResponse());
                    }
                    return feedbackRepository.save(feedback);
                })
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    public void deleteFeedback(Integer id) {
        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
        
        if (!securityService.isSystemAdmin()) {
            securityService.checkUserAccess(feedback.getUserId());
        }
        
        feedbackRepository.deleteById(id);
    }
}
