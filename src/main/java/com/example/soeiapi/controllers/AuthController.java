package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dto.LoginRequestDto;
import com.example.soeiapi.dto.RegisterRequestDto;
import com.example.soeiapi.dto.TokenValidateRequestDto;
import com.example.soeiapi.security.JwtUtil;
import com.example.soeiapi.services.AuthenticationService;
import com.example.soeiapi.services.RoleService;

import org.springframework.http.HttpStatus;
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
    private final RoleService roleService;

    public AuthController(JwtUtil jwtUtil, AuthenticationService authenticationService, RoleService roleService) {
        this.jwtUtil = jwtUtil;
        this.authenticationService = authenticationService;
        this.roleService = roleService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto registerRequestDto) {
        return ResponseEntity.ok(authenticationService.register(registerRequestDto));

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        return ResponseEntity.ok(authenticationService.login(loginRequestDto));
    }

    // Route to validate token and fetch user details (restricted to super-admin
    // only)
    @PostMapping("/validate-token")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> validateTokenAndGetUserInfo(@RequestBody TokenValidateRequestDto tokenValidateRequestDto) {
        System.out.println("Token: " + tokenValidateRequestDto.getToken());
        // Validate the token
        boolean isValid = jwtUtil.isTokenValid(tokenValidateRequestDto.getToken());

        if (isValid) {
            // Extract user details from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Object userDetails = authentication.getPrincipal();
            // You can fetch roles, username, and other info if needed
            return ResponseEntity.ok(userDetails);
        } else {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
    }

    // Get all roles
    @PostMapping("/roles")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

}
