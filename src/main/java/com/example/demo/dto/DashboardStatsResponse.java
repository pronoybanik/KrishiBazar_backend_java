package com.example.demo.dto;
import java.math.BigDecimal;
public record DashboardStatsResponse(long totalUsers, long activeUsers, long totalFarmers, long totalProducts, long totalOrders, long totalReviews, long openReports, BigDecimal totalSales) {}
