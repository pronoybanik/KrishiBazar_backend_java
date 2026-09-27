package com.example.demo.controller;

import java.util.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.demo.dto.*;
import com.example.demo.service.CommerceService;

@RestController
@RequestMapping("/api/v1/admin/orders")
public class OrderAdminController {
    private final CommerceService service;
    public OrderAdminController(CommerceService service) { this.service = service; }
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> all(Authentication a) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "All orders fetched successfully", service.getAllOrders(id(a)))); }
    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<OrderAnalyticsResponse>> analytics(Authentication a) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Order analytics fetched successfully", service.getAdminAnalytics(id(a)))); }
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> status(Authentication a, @PathVariable UUID orderId, @RequestBody Map<String, String> body) { return ResponseEntity.ok(new ApiResponse<>(true, 200, "Order status updated successfully", service.updateStatus(id(a), orderId, body.getOrDefault("status", "")))); }
    private UUID id(Authentication a) { return UUID.fromString(a.getName()); }
}
