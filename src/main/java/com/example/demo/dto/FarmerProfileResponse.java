package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FarmerProfileResponse(
        UUID id,
        UUID userId,
        String farmerName,
        String email,
        String farmName,
        String farmAddress,
        String phoneNumber,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}