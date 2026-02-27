package gio.backend.repository;

import gio.backend.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    List<Feedback> findByRestaurantId(Integer restaurantId);
    List<Feedback> findByUserId(Integer userId);
    List<Feedback> findByRestaurantIdAndIsVisible(Integer restaurantId, Boolean isVisible);
}
