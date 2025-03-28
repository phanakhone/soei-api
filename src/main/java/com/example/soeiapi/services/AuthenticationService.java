package com.example.soeiapi.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dtos.AuthResponse;
import com.example.soeiapi.dtos.LoginRequestDto;
import com.example.soeiapi.dtos.RegisterRequestDto;
import com.example.soeiapi.dtos.UserDto;
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
        // check username and email exists
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequestDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        CompanyEntity company = companyRepository.findById(registerRequestDto.getCompanyId()).orElseThrow(
                () -> new RuntimeException("Company with id " + registerRequestDto.getCompanyId() + " not found"));

        // Get default role (e.g., USER)
        RoleEntity role = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        UserEntity user = new UserEntity();
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRoles(Set.of(role));
        user.setCompany(company);

        // get authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            UserEntity authenticatedUser = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (authenticatedUser != null) {
                user.setCreatedBy(authenticatedUser.getUsername());
                user.setParent(authenticatedUser);
            }
        }

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("status", "success");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", String.join(", ", user.getRoles().stream().map(RoleEntity::getRoleName).toList()));
        return response;
    }

    public AuthResponse login(LoginRequestDto loginRequestDto) {
        UserEntity user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow(
                () -> new RuntimeException("User does not exist"));

        // **Check if user is enabled**
        if (!user.isEnabled()) {
            throw new DisabledException("User account is disabled. Contact admin.");
        }

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        Map<String, String> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail());
        claims.put("role", String.join(", ", user.getRoles().stream().map(RoleEntity::getRoleName).toList()));

        String token = jwtService.generateToken(claims, user.getUsername());
        String refreshToken = userRefreshTokenService.createUserRefreshToken(user);

        // Map<String, String> response = new HashMap<>();
        // response.put("message", "User logged in successfully");
        // response.put("status", "success");
        // response.put("username", user.getUsername());
        // response.put("email", user.getEmail());
        // response.put("role", String.join(", ",
        // user.getRoles().stream().map(RoleEntity::getRoleName).toList()));
        // response.put("token", token);
        // response.put("refreshToken", refreshToken);

        // update last login date time
        userRepository.updateLastLogin(user.getUserId());

        return new AuthResponse(token, refreshToken, UserDto.fromEntity(user));
    }
}
