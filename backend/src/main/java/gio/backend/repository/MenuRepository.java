package gio.backend.repository;

import gio.backend.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
    List<Menu> findByRestaurantId(Integer restaurantId);
    List<Menu> findByRestaurantIdAndIsActive(Integer restaurantId, Boolean isActive);
    List<Menu> findByRestaurantIdAndIsActiveTrue(Integer restaurantId);
}
