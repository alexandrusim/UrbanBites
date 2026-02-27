package gio.backend.config;

import gio.backend.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        
                        .requestMatchers(HttpMethod.GET, "/api/restaurants/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/menus/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/menu-items/**").permitAll()
                        
                        .requestMatchers(HttpMethod.GET, "/api/tables/restaurant/*/available").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tables/restaurant/*").permitAll()
                        
                        .requestMatchers(HttpMethod.POST, "/api/guest/reservations").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/guest/reservations/confirmation/*").permitAll()

                        .requestMatchers("/api/guest/payments/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/feedback/restaurant/**").permitAll()
                        
                        .requestMatchers(HttpMethod.POST, "/api/contact-messages").permitAll()
                        .requestMatchers("/api/contact-messages/**").hasAnyRole("ADMIN_RESTAURANT", "SYSTEM_ADMIN")
                        
                        .requestMatchers("/api/analytics/**").hasAnyRole("ADMIN_RESTAURANT", "SYSTEM_ADMIN")
                        .requestMatchers("/api/activity-logs/**").hasRole("SYSTEM_ADMIN")
                        
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN_RESTAURANT", "SYSTEM_ADMIN")
                        
                        .requestMatchers("/api/reservations/**").authenticated()
                        .requestMatchers("/api/tables/**").authenticated()
                        .requestMatchers("/api/feedback/**").authenticated()
                        .requestMatchers("/api/notifications/**").authenticated()
                        .requestMatchers("/api/payments/**").authenticated()
                        
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
