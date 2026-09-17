package com.example.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.entity.User;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;

@Service
public class ActorService {

    private final UserRepository userRepository;

    public ActorService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User requireUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User requireRole(UUID userId, String role) {
        User user = requireUser(userId);
        if (!role.equals(user.getRole())) {
            throw new ForbiddenException("Only users with the " + role + " role can perform this action");
        }
        if (!Boolean.TRUE.equals(user.getActive())) {
            throw new ForbiddenException("Your account is inactive");
        }
        return user;
    }
}