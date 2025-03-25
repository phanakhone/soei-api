package com.example.soeiapi.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.example.soeiapi.repositories.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

}
