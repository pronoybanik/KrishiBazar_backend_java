package com.example.demo.dto;
import java.time.LocalDateTime; import java.util.UUID;
public record ReviewResponse(UUID id, UUID productId, UUID userId, String userName, Integer rating, String comment, LocalDateTime createdAt, LocalDateTime updatedAt) {}
