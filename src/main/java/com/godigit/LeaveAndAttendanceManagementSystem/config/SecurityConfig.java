// package com.godigit.LeaveAndAttendanceManagementSystem.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {
    
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//         .csrf(csrf -> csrf.disable()) // disable CSRF for APIs (important for POST endpoints)
//         .authorizeHttpRequests(auth -> auth
//             // Public endpoints (if you want some open APIs, like signup/login)
//             .requestMatchers("/auth/**").permitAll()

//             // Employee level
//             .requestMatchers("/punchin", "/punchout", "/applyleave", "/leaves/mine", "/attendance/mine")
//                 .hasAnyRole("EMPLOYEE", "MANAGER", "ADMIN")

//             // Manager level
//             .requestMatchers("/leaves/team", "/approveleave/**", "/rejectleave/**", "/attendance/team")
//                 .hasAnyRole("MANAGER", "ADMIN")

//             // Admin level
//             .requestMatchers("/users/**", "/attendance/all", "/leaves/all")
//                 .hasRole("ADMIN")

//             // everything else must be authenticated
//             .anyRequest().authenticated()
//         )
//         // form-based login (Spring default login page)
//         .formLogin(login -> login.permitAll())
//         .logout(logout -> logout.permitAll());

//         return http.build();
//     }
// }
