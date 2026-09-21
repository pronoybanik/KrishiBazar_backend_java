package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        UUID parentCategoryId,
        String parentCategoryName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
