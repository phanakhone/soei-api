package com.example.soeiapi.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dto.LoginRequestDto;
import com.example.soeiapi.dto.RegisterRequestDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.RoleEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.RoleRepository;
import com.example.soeiapi.repositories.UserRepository;
import com.example.soeiapi.security.JwtUtil;

@Service
public class AuthenticationService {
    private final UserRefreshTokenService userRefreshTokenService;

    public AuthenticationService(UserRefreshTokenService userRefreshTokenService) {
        this.userRefreshTokenService = userRefreshTokenService;
    }

    @Autowired
    private JwtUtil jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Map<String, String> register(RegisterRequestDto registerRequestDto) {
        CompanyEntity company = companyRepository.findByCompanyName(registerRequestDto.getCompany()).orElseThrow();

        // Get default role (e.g., USER)
        RoleEntity role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        UserEntity user = new UserEntity();
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRoles(Set.of(role));
        user.setCompany(company);

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("status", "success");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", String.join(", ", user.getRoles().stream().map(RoleEntity::getRoleName).toList()));
        return response;
    }

    public Map<String, String> login(LoginRequestDto loginRequestDto) {
        UserEntity user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow();

        if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            Map<String, String> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("email", user.getEmail());
            claims.put("role", String.join(", ", user.getRoles().stream().map(RoleEntity::getRoleName).toList()));

            String token = jwtService.generateToken(claims, user.getUsername());
            String refreshToken = userRefreshTokenService.createUserRefreshToken(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User logged in successfully");
            response.put("status", "success");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", String.join(", ", user.getRoles().stream().map(RoleEntity::getRoleName).toList()));
            response.put("token", token);
            response.put("refreshToken", refreshToken);

            return response;
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid credentials");
            response.put("status", "error");
            return response;
        }
    }
}
