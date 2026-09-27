package com.example.demo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public record OrderResponse(UUID id, UUID buyerId, String buyerName, String status, String paymentMethod,
        String paymentReference, BigDecimal totalAmount, AddressResponse address,
        List<OrderItemResponse> items, LocalDateTime createdAt, LocalDateTime updatedAt) {}
