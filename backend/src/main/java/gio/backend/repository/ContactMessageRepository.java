package gio.backend.repository;

import gio.backend.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Integer> {
    List<ContactMessage> findByRestaurantId(Integer restaurantId);
    List<ContactMessage> findByRestaurantIdAndStatus(Integer restaurantId, String status);
}
