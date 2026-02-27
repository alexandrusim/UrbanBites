package gio.backend.service;

import gio.backend.entity.User;
import gio.backend.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserService userService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }
        String email = authentication.getName();
        return userService.getUserByEmail(email);
    }

    public boolean isSystemAdmin() {
        try {
            User currentUser = getCurrentUser();
            return currentUser.getRole() == UserRole.SYSTEM_ADMIN;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRestaurantAdmin() {
        try {
            User currentUser = getCurrentUser();
            return currentUser.getRole() == UserRole.ADMIN_RESTAURANT;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canAccessRestaurant(Integer restaurantId) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser.getRole() == UserRole.SYSTEM_ADMIN) {
                return true;
            }
            if (currentUser.getRole() == UserRole.ADMIN_RESTAURANT) {
                return currentUser.getRestaurantId() != null && 
                       currentUser.getRestaurantId().equals(restaurantId);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canAccessUser(Integer userId) {
        try {
            User currentUser = getCurrentUser();
            return currentUser.getRole() == UserRole.SYSTEM_ADMIN || 
                   currentUser.getUserId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    public void checkRestaurantAccess(Integer restaurantId) {
        if (!canAccessRestaurant(restaurantId)) {
            throw new RuntimeException("Access denied: You can only manage your own restaurant");
        }
    }

    public void checkUserAccess(Integer userId) {
        if (!canAccessUser(userId)) {
            throw new RuntimeException("Access denied: You can only access your own data");
        }
    }
}
