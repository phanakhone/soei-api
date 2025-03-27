package com.example.soeiapi.services;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.soeiapi.entities.UserEntity;
import com.example.soeiapi.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // get all users
    public Page<UserEntity> getAllUsers(PageRequest pageRequest, Map<String, String> filters) {
        return userRepository.findAll(pageRequest);
    }
}
