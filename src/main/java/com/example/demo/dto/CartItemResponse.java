package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(UUID id, UUID productId, String productName, BigDecimal unitPrice,
        BigDecimal quantity, BigDecimal lineTotal, String unit) {}
