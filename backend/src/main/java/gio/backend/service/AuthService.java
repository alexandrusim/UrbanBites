package gio.backend.service;

import gio.backend.dto.AuthResponse;
import gio.backend.dto.LoginRequest;
import gio.backend.dto.RegisterRequest;
import gio.backend.entity.User;
import gio.backend.enums.UserRole;
import gio.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(UserRole.CLIENT);
        user.setIsActive(true);
        user.setEmailVerified(false);

        User savedUser = userService.createUser(user);

        String token = jwtUtil.generateToken(
            savedUser.getEmail(), 
            savedUser.getRole().name(), 
            savedUser.getUserId()
        );

        return new AuthResponse(
            token,
            savedUser.getEmail(),
            savedUser.getRole().name(),
            savedUser.getUserId(),
            savedUser.getFirstName(),
            savedUser.getLastName(),
            savedUser.getTwoFaEnabled(),
            savedUser.getTwoFaVerified()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userService.getUserByEmail(request.getEmail());

        String token = jwtUtil.generateToken(
            user.getEmail(), 
            user.getRole().name(), 
            user.getUserId()
        );

        return new AuthResponse(
            token,
            user.getEmail(),
            user.getRole().name(),
            user.getUserId(),
            user.getFirstName(),
            user.getLastName(),
            user.getTwoFaEnabled(),
            user.getTwoFaVerified()
        );
    }

    public User getCurrentUser(String email) {
        return userService.getUserByEmail(email);
    }
}
