package com.example.soeiapi.services;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.soeiapi.configs.AuthenticatedUser;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AuthenticatedUser findMatch(String username) {
        Optional<UserEntity> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            logger.info("User not found: " + username);
            throw new UsernameNotFoundException("Username " + username + " doesn't exits");
        }

        logger.info("User Instance: " + username);

        GrantedAuthority authority = () -> {
            return user.get().getRole().getRoleName();
        };

        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>(Arrays.asList(authority));
        return new AuthenticatedUser(user.get(), authorities);
    }

    public UserEntity getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        return (UserEntity) authentication.getPrincipal();
    }
}
