package gio.backend.service;

import gio.backend.dto.MenuDTO;
import gio.backend.entity.Menu;
import gio.backend.repository.MenuRepository;
import gio.backend.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final SecurityService securityService;

    @Transactional(readOnly = true)
    public List<MenuDTO> getAllMenus() {
        return menuRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuDTO getMenuById(Integer id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meniul cu ID " + id + " nu a fost găsit"));
        return convertToDTO(menu);
    }

    @Transactional(readOnly = true)
    public List<MenuDTO> getMenusByRestaurantId(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RuntimeException("Restaurantul cu ID " + restaurantId + " nu există");
        }
        return menuRepository.findByRestaurantId(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MenuDTO> getActiveMenusByRestaurantId(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RuntimeException("Restaurantul cu ID " + restaurantId + " nu există");
        }
        return menuRepository.findByRestaurantIdAndIsActiveTrue(restaurantId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuDTO createMenu(MenuDTO menuDTO) {
        if (!restaurantRepository.existsById(menuDTO.getRestaurantId())) {
            throw new RuntimeException("Restaurantul cu ID " + menuDTO.getRestaurantId() + " nu există");
        }
        
        if (!securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(menuDTO.getRestaurantId());
        }

        Menu menu = convertToEntity(menuDTO);
        Menu savedMenu = menuRepository.save(menu);
        return convertToDTO(savedMenu);
    }

    @Transactional
    public MenuDTO updateMenu(Integer id, MenuDTO menuDTO) {
        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meniul cu ID " + id + " nu a fost găsit"));

        if (!securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(existingMenu.getRestaurantId());
        }

        if (!restaurantRepository.existsById(menuDTO.getRestaurantId())) {
            throw new RuntimeException("Restaurantul cu ID " + menuDTO.getRestaurantId() + " nu există");
        }

        existingMenu.setRestaurantId(menuDTO.getRestaurantId());
        existingMenu.setName(menuDTO.getName());
        existingMenu.setDescription(menuDTO.getDescription());
        existingMenu.setIsActive(menuDTO.getIsActive());
        existingMenu.setAvailableFrom(menuDTO.getAvailableFrom());
        existingMenu.setAvailableTo(menuDTO.getAvailableTo());
        existingMenu.setValidDays(menuDTO.getValidDays());

        Menu updatedMenu = menuRepository.save(existingMenu);
        return convertToDTO(updatedMenu);
    }

    @Transactional
    public void deleteMenu(Integer id) {
        Menu menu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Meniul cu ID " + id + " nu a fost găsit"));
        
        if (!securityService.isSystemAdmin()) {
            securityService.checkRestaurantAccess(menu.getRestaurantId());
        }
        
        menuRepository.deleteById(id);
    }

    private MenuDTO convertToDTO(Menu menu) {
        MenuDTO dto = new MenuDTO();
        dto.setMenuId(menu.getMenuId());
        dto.setRestaurantId(menu.getRestaurantId());
        dto.setName(menu.getName());
        dto.setDescription(menu.getDescription());
        dto.setIsActive(menu.getIsActive());
        dto.setAvailableFrom(menu.getAvailableFrom());
        dto.setAvailableTo(menu.getAvailableTo());
        dto.setValidDays(menu.getValidDays());
        dto.setCreatedAt(menu.getCreatedAt());
        dto.setUpdatedAt(menu.getUpdatedAt());
        return dto;
    }

    private Menu convertToEntity(MenuDTO dto) {
        Menu menu = new Menu();
        menu.setRestaurantId(dto.getRestaurantId());
        menu.setName(dto.getName());
        menu.setDescription(dto.getDescription());
        menu.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        menu.setAvailableFrom(dto.getAvailableFrom());
        menu.setAvailableTo(dto.getAvailableTo());
        menu.setValidDays(dto.getValidDays());
        return menu;
    }
}
