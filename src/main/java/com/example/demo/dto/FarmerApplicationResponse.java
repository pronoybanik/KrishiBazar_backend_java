package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.entity.FarmerApplication.Status;

public record FarmerApplicationResponse(
        UUID id,
        UUID userId,
        String farmName,
        String farmAddress,
        String phoneNumber,
        String description,
        Status status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}