package com.example.soeiapi.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dto.LoginRequestDto;
import com.example.soeiapi.dto.RegisterRequestDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.RoleRepository;
import com.example.soeiapi.repositories.UserRepository;

@Service
public class AuthenticationService {
    @Autowired
    private JwtService jwtService;

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

        UserEntity user = new UserEntity();
        user.setUsername(registerRequestDto.getUsername());
        user.setEmail(registerRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
        user.setRole(roleRepository.findByRoleName("USER").get());
        user.setCompany(company);

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("status", "success");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().getRoleName());
        return response;
    }

    public Map<String, String> login(LoginRequestDto loginRequestDto) {
        UserEntity user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow();

        if (passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            Map<String, String> claims = new HashMap<>();
            claims.put("username", user.getUsername());
            claims.put("email", user.getEmail());
            claims.put("role", user.getRole().getRoleName());
            String token = jwtService.generateToken(claims, user.getUsername());
            String refreshToken = jwtService.createUserRefreshToken(user);

            Map<String, String> response = new HashMap<>();
            response.put("message", "User logged in successfully");
            response.put("status", "success");
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().getRoleName());
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
