package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.Map;

public record FarmerDashboardStatsResponse(long totalOrders, long deliveredOrders,
        long pendingOrders, BigDecimal totalSales, BigDecimal deliveredSales,
        BigDecimal totalItemsSold, Map<String, Long> ordersByStatus) {}
