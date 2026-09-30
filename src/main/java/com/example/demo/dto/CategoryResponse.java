package com.example.demo.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;

public record CategoryResponse(
        UUID id,
        String name,
        UUID parentCategoryId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL) List<CategoryResponse> children
) {
}
