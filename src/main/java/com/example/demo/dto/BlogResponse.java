package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record BlogResponse(
        UUID id,
        String title,
        String content,
        String imageUrl,
        UUID authorId,
        String authorName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
