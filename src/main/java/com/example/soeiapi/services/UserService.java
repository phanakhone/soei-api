package com.example.soeiapi.services;

import java.util.Map;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.soeiapi.dtos.UpdateUserRequestDto;
import com.example.soeiapi.entities.CompanyEntity;
import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.CompanyRepository;
import com.example.soeiapi.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    UserService(UserRepository userRepository, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
    }

    // private final Logger logger = LoggerFactory.getLogger(getClass());

    // get all users
    public Page<UserEntity> getAllUsers(PageRequest pageRequest, Map<String, String> filters) {
        if (filters != null && !filters.isEmpty()) {
            Long companyId = filters.get("companyId") != null ? Long.valueOf(filters.get("companyId")) : null;
            if (companyId != null) {
                CompanyEntity company = companyRepository.findById(companyId).orElse(null);

                return userRepository.findByCompany(company, pageRequest);
            }

        }
        return userRepository.findAll(pageRequest);
    }

    public Page<UserEntity> getAllChildUsersExcludingSameLevel(
            Long userId, PageRequest pageRequest, Map<String, String> filters) {
        return userRepository.findAllChildUsersExcludingSameLevel(userId, pageRequest);
    }

    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    public Integer getUserLevel(Long userId) {
        return userRepository.findUserLevel(userId);
    }

    // update a user
    public UserEntity updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        userEntity.setEmail(updateUserRequestDto.getEmail());
        userEntity.setPhoneNumber(updateUserRequestDto.getPhoneNumber());

        return userRepository.save(userEntity);
    }

    // toggle is enabled to user
    @Transactional
    public UserEntity toggleUserEnabled(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setEnabled(!user.isEnabled()); // Toggle status
        return userRepository.save(user);
    }
}
