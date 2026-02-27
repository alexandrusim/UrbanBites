package gio.backend.repository;

import gio.backend.entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableRepository extends JpaRepository<Table, Integer> {
    List<Table> findByRestaurantId(Integer restaurantId);
    List<Table> findByRestaurantIdAndIsAvailable(Integer restaurantId, Boolean isAvailable);
}
