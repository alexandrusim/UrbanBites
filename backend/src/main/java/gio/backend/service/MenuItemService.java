package gio.backend.service;

import gio.backend.dto.MenuItemDTO;
import gio.backend.entity.MenuItem;
import gio.backend.repository.MenuItemRepository;
import gio.backend.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getAllMenuItems() {
        return menuItemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuItemDTO getMenuItemById(Integer id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produsul cu ID " + id + " nu a fost găsit"));
        return convertToDTO(menuItem);
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getMenuItemsByMenuId(Integer menuId) {
        if (!menuRepository.existsById(menuId)) {
            throw new RuntimeException("Meniul cu ID " + menuId + " nu există");
        }
        return menuItemRepository.findByMenuId(menuId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getAvailableMenuItemsByMenuId(Integer menuId) {
        if (!menuRepository.existsById(menuId)) {
            throw new RuntimeException("Meniul cu ID " + menuId + " nu există");
        }
        return menuItemRepository.findByMenuIdAndIsAvailableTrue(menuId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemDTO createMenuItem(MenuItemDTO menuItemDTO) {
        if (!menuRepository.existsById(menuItemDTO.getMenuId())) {
            throw new RuntimeException("Meniul cu ID " + menuItemDTO.getMenuId() + " nu există");
        }

        var menu = menuRepository.findById(menuItemDTO.getMenuId())
                .orElseThrow(() -> new RuntimeException("Meniul nu a fost găsit"));
        
        if (!securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(menu.getRestaurantId());
        }

        MenuItem menuItem = convertToEntity(menuItemDTO);
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return convertToDTO(savedMenuItem);
    }

    @Transactional
    public MenuItemDTO updateMenuItem(Integer id, MenuItemDTO menuItemDTO) {
        MenuItem existingMenuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produsul cu ID " + id + " nu a fost găsit"));

        var menu = menuRepository.findById(existingMenuItem.getMenuId())
                .orElseThrow(() -> new RuntimeException("Meniul nu a fost găsit"));
        
        if (!securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(menu.getRestaurantId());
        }

        if (!menuRepository.existsById(menuItemDTO.getMenuId())) {
            throw new RuntimeException("Meniul cu ID " + menuItemDTO.getMenuId() + " nu există");
        }

        existingMenuItem.setMenuId(menuItemDTO.getMenuId());
        existingMenuItem.setName(menuItemDTO.getName());
        existingMenuItem.setDescription(menuItemDTO.getDescription());
        existingMenuItem.setPrice(menuItemDTO.getPrice());
        existingMenuItem.setCategory(menuItemDTO.getCategory());
        existingMenuItem.setImageUrl(menuItemDTO.getImageUrl());
        existingMenuItem.setIsAvailable(menuItemDTO.getIsAvailable());
        existingMenuItem.setAllergens(menuItemDTO.getAllergens());

        MenuItem updatedMenuItem = menuItemRepository.save(existingMenuItem);
        return convertToDTO(updatedMenuItem);
    }

    @Transactional
    public void deleteMenuItem(Integer id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produsul cu ID " + id + " nu a fost găsit"));
        
        var menu = menuRepository.findById(menuItem.getMenuId())
                .orElseThrow(() -> new RuntimeException("Meniul nu a fost găsit"));
        
        if (!securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(menu.getRestaurantId());
        }
        
        menuItemRepository.deleteById(id);
    }

    private MenuItemDTO convertToDTO(MenuItem menuItem) {
        MenuItemDTO dto = new MenuItemDTO();
        dto.setItemId(menuItem.getItemId());
        dto.setMenuId(menuItem.getMenuId());
        dto.setName(menuItem.getName());
        dto.setDescription(menuItem.getDescription());
        dto.setPrice(menuItem.getPrice());
        dto.setCategory(menuItem.getCategory());
        dto.setImageUrl(menuItem.getImageUrl());
        dto.setIsAvailable(menuItem.getIsAvailable());
        dto.setAllergens(menuItem.getAllergens());
        dto.setCreatedAt(menuItem.getCreatedAt());
        dto.setUpdatedAt(menuItem.getUpdatedAt());
        return dto;
    }

    private MenuItem convertToEntity(MenuItemDTO dto) {
        MenuItem menuItem = new MenuItem();
        menuItem.setMenuId(dto.getMenuId());
        menuItem.setName(dto.getName());
        menuItem.setDescription(dto.getDescription());
        menuItem.setPrice(dto.getPrice());
        menuItem.setCategory(dto.getCategory());
        menuItem.setImageUrl(dto.getImageUrl());
        menuItem.setIsAvailable(dto.getIsAvailable() != null ? dto.getIsAvailable() : true);
        menuItem.setAllergens(dto.getAllergens());
        return menuItem;
    }
}
