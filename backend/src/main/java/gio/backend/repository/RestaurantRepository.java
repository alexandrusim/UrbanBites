package gio.backend.repository;

import gio.backend.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    List<Restaurant> findByCity(String city);
    List<Restaurant> findByCuisineType(String cuisineType);
    List<Restaurant> findByIsActive(Boolean isActive);
}
