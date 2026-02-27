package gio.backend.service;

import gio.backend.dto.UserDTO;
import gio.backend.entity.User;
import gio.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    public User updateUser(Integer id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    if (userDetails.getFirstName() != null) {
                        user.setFirstName(userDetails.getFirstName());
                    }
                    if (userDetails.getLastName() != null) {
                        user.setLastName(userDetails.getLastName());
                    }
                    if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
                        if (userRepository.existsByEmail(userDetails.getEmail())) {
                            throw new RuntimeException("Email already exists: " + userDetails.getEmail());
                        }
                        user.setEmail(userDetails.getEmail());
                    }
                    if (userDetails.getPhoneNumber() != null) {
                        user.setPhoneNumber(userDetails.getPhoneNumber());
                    }
                    if (userDetails.getRole() != null) {
                        user.setRole(userDetails.getRole());
                    }
                    if (userDetails.getIsActive() != null) {
                        user.setIsActive(userDetails.getIsActive());
                    }
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
    
    public User updateUserRole(Integer id, gio.backend.enums.UserRole role) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setRole(role);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public long countAllUsers() {
        return userRepository.count();
    }

    public List<UserDTO> getAllUsersDTO() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserDTOById(Integer id) {
        return userRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getUserId(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPhoneNumber(),
            user.getRole(),
            user.getRestaurantId(),
            user.getIsActive(),
            user.getCreatedAt(),
            user.getLastLoginAt()
        );
    }
}
