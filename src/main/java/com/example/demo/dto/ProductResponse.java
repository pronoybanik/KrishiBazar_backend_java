package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        UUID farmerId,
        String farmerName,
        UUID categoryId,
        String categoryName,
        UUID parentCategoryId,
        String name,
        String description,
        BigDecimal price,
        BigDecimal quantity,
        String unit,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}