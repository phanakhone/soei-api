package com.example.soeiapi.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.soeiapi.dtos.AdminResetPasswordDto;
import com.example.soeiapi.dtos.ApiResponse;
import com.example.soeiapi.dtos.AuthResponse;
import com.example.soeiapi.dtos.LoginRequestDto;
import com.example.soeiapi.dtos.RefreshTokenRequestDto;
import com.example.soeiapi.dtos.RegisterRequestDto;
import com.example.soeiapi.dtos.RequestResetPasswordDto;
import com.example.soeiapi.dtos.ResetPasswordWithTokenDto;
import com.example.soeiapi.dtos.TokenValidateRequestDto;
import com.example.soeiapi.dtos.UserDto;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.security.JwtUtil;
import com.example.soeiapi.services.AuthenticationService;
import com.example.soeiapi.services.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthController(JwtUtil jwtUtil, AuthenticationService authenticationService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/register")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequestDto registerRequestDto) {
        AuthResponse authResponse = authenticationService.register(registerRequestDto);
        return ResponseEntity.ok(ApiResponse.success("User register successfully", authResponse));

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
    public ResponseEntity<ApiResponse<UserDto>> validateTokenAndGetUserInfo(
            @RequestBody TokenValidateRequestDto tokenValidateRequestDto) {
        System.out.println("Token: " + tokenValidateRequestDto.getToken());
        // Validate the token

        if (!jwtUtil.isValidJwt(tokenValidateRequestDto.getToken())) {
            throw new RuntimeException("[validate-token] Invalid jwt token");
        }

        if (!jwtUtil.isTokenValid(tokenValidateRequestDto.getToken())) {
            throw new RuntimeException("Invalid token or expired");
        }

        // Get user detail from token
        UserEntity user = userService.getUserByUsername(jwtUtil.extractUserName(tokenValidateRequestDto.getToken()));

        return ResponseEntity.ok(ApiResponse.success("Token is valid", UserDto.fromEntity(user)));

    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestBody RefreshTokenRequestDto refreshTokenRequestDto) {
        AuthResponse authResponse = authenticationService.refreshToken(refreshTokenRequestDto.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
    }

    // send request password
    @PostMapping("/request-password-reset")
    public ResponseEntity<ApiResponse<String>> requestPasswordReset(
            @RequestBody RequestResetPasswordDto requestResetPasswordDto) {
        authenticationService.requestPasswordReset(requestResetPasswordDto.getUserId(),
                requestResetPasswordDto.getTokenExpiryMinutes());
        return ResponseEntity.ok(ApiResponse.success("Password reset request sent successfully", null));
    }

    // reset password
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @RequestBody ResetPasswordWithTokenDto resetPasswordtDto) {
        authenticationService.resetPasswordWithToken(resetPasswordtDto);
        return ResponseEntity.ok(ApiResponse.success("Password reset successfully", null));
    }

    @PostMapping("/admin/reset-password")
    @PreAuthorize("hasRole('SUPER_ADMIN') or hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<ApiResponse<String>> resetPasswordByAdmin(
            @RequestBody @Valid AdminResetPasswordDto adminResetPasswordDto) {
        // get auth user
        UserDetails authUser = authenticationService.getAuthenticatedPrincipal();
        authenticationService.resetPasswordByAdmin(authUser.getUsername(), adminResetPasswordDto);

        return ResponseEntity.ok(ApiResponse.success("Password reset successfully by admin", null));
    }

}
