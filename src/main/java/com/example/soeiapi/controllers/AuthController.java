package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.dtos.AuthResponse;
import com.example.soeiapi.dtos.LoginRequestDto;
import com.example.soeiapi.dtos.RegisterRequestDto;
import com.example.soeiapi.dtos.TokenValidateRequestDto;
import com.example.soeiapi.security.JwtUtil;
import com.example.soeiapi.services.AuthenticationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;

    public AuthController(JwtUtil jwtUtil, AuthenticationService authenticationService) {
        this.jwtUtil = jwtUtil;
        this.authenticationService = authenticationService;

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(authenticationService.register(registerRequestDto));

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequestDto loginRequestDto) {
        AuthResponse authResponse = authenticationService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    // Route to validate token and fetch user details (restricted to super-admin
    // only)
    @PostMapping("/validate-token")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> validateTokenAndGetUserInfo(@RequestBody TokenValidateRequestDto tokenValidateRequestDto) {
        System.out.println("Token: " + tokenValidateRequestDto.getToken());
        // Validate the token

        if (!jwtUtil.isValidJwt(tokenValidateRequestDto.getToken())) {
            throw new RuntimeException("Invalid jwt token");
        }

        if (!jwtUtil.isTokenValid(tokenValidateRequestDto.getToken())) {
            throw new RuntimeException("Invalid token or expired");
        }
        // Extract user details from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object userDetails = authentication.getPrincipal();
        // You can fetch roles, username, and other info if needed
        return ResponseEntity.ok(userDetails);

    }

}
