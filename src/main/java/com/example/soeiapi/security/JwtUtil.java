package com.example.soeiapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Component;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.services.UserService;

@Component
public class JwtUtil {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    // @Autowired
    // private UserRefreshTokenRepository userRefreshTokenRepository;

    private final UserService userService;

    public JwtUtil(UserService userService) {
        this.userService = userService;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Map<String, String> extraClaims, String username) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUserName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Long extractIssueAt(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getIssuedAt().getTime();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public boolean isValidJwt(String token) {
        try {
            Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("expired jwt token: " + e.getMessage());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        Long tokenIssueAt = extractIssueAt(token);
        String username = extractUserName(token);

        UserEntity user = userService.getUserByUsername(username);

        // check last password change date with token issue date
        Long lastPasswordChange = user.getLastPasswordChangeAt() != null
                ? user.getLastPasswordChangeAt().toEpochMilli()
                : 0L;

        // if lastPasswordChange = 0L then true else lastPasswordChange < tokenIssueAt
        // !isExpired
        return (lastPasswordChange == 0L || lastPasswordChange < tokenIssueAt)
                && !isTokenExpired(token);
    }

    private Claims extractAllClaims(String token) {
        // Extract claims after signature verification
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
