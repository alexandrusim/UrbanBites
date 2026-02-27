package gio.backend.repository;

import gio.backend.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
    List<MenuItem> findByMenuId(Integer menuId);
    List<MenuItem> findByMenuIdAndIsAvailable(Integer menuId, Boolean isAvailable);
    List<MenuItem> findByMenuIdAndIsAvailableTrue(Integer menuId);
    List<MenuItem> findByCategory(String category);
}
