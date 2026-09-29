package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record OrderItemRequest(
        @NotNull UUID productId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal quantity
) {}
