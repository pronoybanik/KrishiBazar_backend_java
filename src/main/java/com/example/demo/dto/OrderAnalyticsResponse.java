package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Map;

public record OrderAnalyticsResponse(
        long totalOrders,
        BigDecimal totalSales,
        BigDecimal totalItemsSold,
        Map<String, Long> ordersByStatus,
        Map<String, BigDecimal> salesByPaymentMethod
) {
}
