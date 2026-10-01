package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(UUID productId, UUID farmerId, String productName, BigDecimal unitPrice,
        BigDecimal quantity, BigDecimal lineTotal, String unit, FarmerDetailsResponse farmer) {}
