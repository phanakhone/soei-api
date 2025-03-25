package com.example.soeiapi.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    JwtAuthFilter jwtAuthFilter;

    UserDetailsService userDetailsService;

    public SecurityConfiguration(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService,
            ApplicationConfiguration applicationConfiguration) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain basicAuthSecurityFilterChain(HttpSecurity http) throws Exception {
        try {
            return http.csrf(csrf -> csrf.disable())
                    .authorizeHttpRequests(
                            req -> req
                                    .requestMatchers("/public/**", "/api/auth/register", "/api/auth/login").permitAll()
                                    .anyRequest().authenticated())
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Error configuring HttpSecurity", e);
        }

    }
}
