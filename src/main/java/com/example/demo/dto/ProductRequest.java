package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 1000) String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal price,
        @NotNull @DecimalMin(value = "0.01") BigDecimal quantity,
        @NotBlank @Size(max = 30) String unit,
        @Size(max = 500) String imageUrl,
        @NotNull UUID categoryId
) {
}