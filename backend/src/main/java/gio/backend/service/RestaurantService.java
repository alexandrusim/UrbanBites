package gio.backend.service;

import gio.backend.entity.Restaurant;
import gio.backend.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RestaurantService {

    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @Autowired
    private SecurityService securityService;

    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    public Optional<Restaurant> getRestaurantById(Integer id) {
        return restaurantRepository.findById(id);
    }

    public List<Restaurant> getRestaurantsByCity(String city) {
        return restaurantRepository.findByCity(city);
    }

    public List<Restaurant> getRestaurantsByCuisineType(String cuisineType) {
        return restaurantRepository.findByCuisineType(cuisineType);
    }

    public List<Restaurant> getActiveRestaurants() {
        return restaurantRepository.findByIsActive(true);
    }

    public Restaurant createRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    public Restaurant updateRestaurant(Integer id, Restaurant restaurantDetails) {
        return restaurantRepository.findById(id)
                .map(restaurant -> {
                    if (!securityService.isSystemAdmin()) {
                        securityService.checkRestaurantAccess(restaurant.getRestaurantId());
                    }
                    
                    if (restaurantDetails.getName() != null) {
                        restaurant.setName(restaurantDetails.getName());
                    }
                    if (restaurantDetails.getDescription() != null) {
                        restaurant.setDescription(restaurantDetails.getDescription());
                    }
                    if (restaurantDetails.getAddress() != null) {
                        restaurant.setAddress(restaurantDetails.getAddress());
                    }
                    if (restaurantDetails.getCity() != null) {
                        restaurant.setCity(restaurantDetails.getCity());
                    }
                    if (restaurantDetails.getPhoneNumber() != null) {
                        restaurant.setPhoneNumber(restaurantDetails.getPhoneNumber());
                    }
                    if (restaurantDetails.getEmail() != null) {
                        restaurant.setEmail(restaurantDetails.getEmail());
                    }
                    if (restaurantDetails.getCuisineType() != null) {
                        restaurant.setCuisineType(restaurantDetails.getCuisineType());
                    }
                    if (restaurantDetails.getCapacity() != null) {
                        restaurant.setCapacity(restaurantDetails.getCapacity());
                    }
                    if (restaurantDetails.getOpeningHours() != null) {
                        restaurant.setOpeningHours(restaurantDetails.getOpeningHours());
                    }
                    if (restaurantDetails.getIsActive() != null) {
                        restaurant.setIsActive(restaurantDetails.getIsActive());
                    }
                    return restaurantRepository.save(restaurant);
                })
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
    }

    public void deleteRestaurant(Integer id) {
        restaurantRepository.deleteById(id);
    }

    public long countAllRestaurants() {
        return restaurantRepository.count();
    }
}
