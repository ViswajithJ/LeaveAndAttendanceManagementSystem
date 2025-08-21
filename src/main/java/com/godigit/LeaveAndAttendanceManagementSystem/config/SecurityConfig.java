package com.godigit.LeaveAndAttendanceManagementSystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import com.godigit.LeaveAndAttendanceManagementSystem.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // Enables @PreAuthorize, @RolesAllowed etc
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordConfig  passwordConfig;

    // --- DaoAuthenticationProvider bean ---
    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordConfig.passwordEncoder());
        return authProvider;
    }

    // --- AuthenticationManager bean (fixed) ---
    @Bean
    public AuthenticationManager authenticationManager(DaoAuthenticationProvider authProvider) {
        return new ProviderManager(authProvider);
    }

    // --- Security filter chain ---
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**",  "/h2-console/**").permitAll() // signup/login are public
                .anyRequest().authenticated()
            )
            .headers(headers -> headers
                    .frameOptions(frame -> frame.disable())
            )
            // .sessionManagement(session -> session
            //     .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            // );
            .formLogin(form -> form
            .loginProcessingUrl("/api/auth/login") // POST request goes here
            .usernameParameter("email")     // 👈 tells Spring to look for "email"
            .passwordParameter("password")
            .successHandler((request, response, authentication) -> {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Login successful\"}");
            })
            .failureHandler((request, response, exception) -> {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid username or password\"}");
            })
            .permitAll()
        )

            .logout(logout -> logout.permitAll());
        return http.build();
    }
}
